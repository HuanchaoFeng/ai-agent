package com.yupi.yuaiagent.manus.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import com.yupi.yuaiagent.manus.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{

    private final ToolCallback[] availableTools;

    private ChatResponse toolCallChatResponse;

    private final ToolCallingManager toolCallingManager;

    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        //withProxyToolCalls 选项设置为true，来禁止 Spring AI 托管工具调用:LLM 只是生成“调用意图”，工具的执行和结果传递由外部代理（程序）完成
        this.chatOptions = DashScopeChatOptions.builder().withProxyToolCalls(true).build();
    }

    /**
     *  传入工具列表，并调用大模型，得到需要调用的工具列表
     *  主要流程：创建对话（把工具、prompt全都放进去）——> 得到llm的答案：是不是需要调用工具 ——>需要调用工具就
     * @return
     */
    @Override
    public boolean think() {
        if(getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try{
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            this.toolCallChatResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName() + "的思考： " + result);
            log.info(getName() + "选择了： " + toolCallList.size() + "个工具来使用");
            //工具转换
            String toolCallInfo = toolCallList.stream().map(toolCall -> String.format("工具名称：%s, 参数：%s",toolCall.name(),toolCall.arguments())).collect(Collectors.joining("\n"));
            log.info(toolCallInfo);

            if(toolCallList.isEmpty()){
                getMessageList().add(assistantMessage);
                return false;
            } else {
                return true;
            }

        }catch (Exception e){
            log.error(getName() + "的思考过程遇到问题： " + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到错误" + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        if(!toolCallChatResponse.hasToolCalls()){
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(), chatOptions);

        //执行工具调用
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);//toolcallchatresponse是本轮的llm回复
        //把调用的结果存入对话历史message中，留到下次循环和llm对话时用
        setMessageList(toolExecutionResult.conversationHistory());
        //取出最后一个工具响应消息
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));

        //当调用了终止工具时，修改agent状态为已结束
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if(terminateToolCalled){
            setState(AgentState.FINISHED);
        }
        log.info(results);
        return results;
    }
}
