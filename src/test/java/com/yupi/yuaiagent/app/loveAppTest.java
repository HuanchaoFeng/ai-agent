package com.yupi.yuaiagent.app;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class loveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    public void testChat() {
        String message = "hello, I'm phoenix";
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "I miss my girl friend";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "what's my name?";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    public void testChatLoveReport() {
        String message = "hello, I'm phoenix";
        String chatId = UUID.randomUUID().toString();
        LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);

    }

    @Test
    public void testChatRAG() {
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithRAG(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
