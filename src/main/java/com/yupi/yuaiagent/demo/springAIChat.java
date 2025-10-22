package com.yupi.yuaiagent.demo;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class springAIChat  implements CommandLineRunner {

    @Resource
    private ChatModel dashScopeChatModel;

    @Override
    public void run(String... args) throws Exception {

        String assistantMessage = dashScopeChatModel.call(new Prompt("hello")).getResult().getOutput().getText();
        System.out.printf(assistantMessage);

    }
}
