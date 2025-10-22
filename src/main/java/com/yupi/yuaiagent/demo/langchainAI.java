package com.yupi.yuaiagent.demo;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class langchainAI {

    public static void main(String[] args) {
        QwenChatModel model = QwenChatModel.builder().apiKey("sk-1db1e7fea48546b483df309d33fb181c").modelName("qwen-plus").build();
        String answer = model.chat("hello");
        System.out.println(answer);
    }
}
