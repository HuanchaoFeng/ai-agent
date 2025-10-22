package com.yupi.yuaiagent.app;



import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import com.yupi.yuaiagent.advisor.ReReadingAdvisor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 *      基于内存模型的基础多轮对话
 */
@Component
@Slf4j
public class loveApp {
    /**
     *  首先初始化 chatclient 对象。
     *  使用 Spring 的构造器注入方‌式来注入阿里大模型 dashscopeChatModel对象，
     *  并使用该对象来初始化 Chatclient。
     *  初始化时指定默认的系统 Prompt 和基于内存‌的对话记忆 Advisor。
     */
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    //构建聊天客户端
    public loveApp(ChatModel dashscopeChatModel){

        ChatMemory chatMemory = new InMemoryChatMemory();//保存对话记忆
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new ReReadingAdvisor()
//                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * 编写对话方法
     * 调用 chatClient 对象，传入用户 Prompt，
     * 并且给 advisor 指定对话 id 和对话‌记忆大小。代码如下：
     * @return
     */

    public String doChat(String message, String chatId){
        //下面的advisors传参的时候，是把param放进一个全局的上下文参数表，所有advisors都能访问这个表，并读取自己要用的Key
        ChatResponse chatResponse = chatClient.prompt()
                                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                                .call()
                                .chatResponse();
        String context = chatResponse.getResult().getOutput().getText();
        log.info("model response context={}",context);
        return context;
    }



}
