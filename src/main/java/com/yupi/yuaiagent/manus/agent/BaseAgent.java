package com.yupi.yuaiagent.manus.agent;

import com.yupi.yuaiagent.manus.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *  该类为模板，定义规范，定义一个模型的整体流转，把模型执行过程和清理交给子类去具体实现
 */
@Data
@Slf4j
public abstract class BaseAgent {

    private String name;
    private String systemPrompt;
    private String nextStepPrompt;
    private AgentState state = AgentState.IDLE;

    private int maxSteps = 10;
    private int currentStep = 0;
    private ChatClient chatClient;

    private List<Message> messageList = new ArrayList<>();

    public String run(String userPrompt){
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if(StringUtil.isEmpty(userPrompt)){
            throw new RuntimeException("Cannot run agent with empty user prompy");

        }

        state = AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));

        List<String> results = new ArrayList<>();

        try{
            for(int i = 0; i < maxSteps && state != AgentState.FINISHED; i++){
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);

                String stepResult = step(); //具体实现,同时在step里面进行终止操作
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }

            if(currentStep >= maxSteps){
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        }catch(Exception e){
            state = AgentState.ERROR;
            log.error("Error executing agent step", e);
            return "Error executing agent step : " + e.getMessage();

        }finally {
            this.cleanup();
        }

    }

    public abstract String step();

    protected void cleanup(){

    }

    /**
     *      改成同步为流式结果，只需要加上sse send，一个send一个complete即可
     * @param userPrompt
     * @return
     */
    public SseEmitter runStream(String userPrompt){

        SseEmitter emitter = new SseEmitter(300000L);
        //使用completablefuture进行异步推送，避免长期占用web资源
        CompletableFuture.runAsync(()->{
            try{
                if(this.state != AgentState.IDLE){
                    emitter.send("错误，无法从状态运行代理： " +this.state);
                    emitter.complete();
                    return;
                }
                if(StringUtil.isEmpty(userPrompt)){
                    emitter.send("错误： 不能使用空提示词");
                    emitter.complete();
                    return;
                }
                state = AgentState.RUNNING;
                messageList.add(new UserMessage(userPrompt));

                try {
                    for(int i = 0; i < maxSteps && state != AgentState.FINISHED; i++){
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxSteps);
                        String stepResult = step();
                        emitter.send(stepResult);
                    }
                    if(currentStep >= maxSteps){
                        state = AgentState.FINISHED;
                        emitter.send("执行结束：达到最大步骤");
                    }
                    emitter.complete();
                }catch (Exception e){
                    state = AgentState.ERROR;
                    log.error("Error executing agent step", e);
                    try {
                        emitter.send("执行错误： " + e.getMessage());
                        emitter.complete();
                    }catch (Exception e1){
                        emitter.completeWithError(e1);
                    }
                }finally {
                    this.cleanup();
                }
            }catch(Exception e){
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return emitter;

    }


































}
