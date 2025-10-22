package com.yupi.yuaiagent.app;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class loveAppTest {

    @Resource
    private loveApp  loveApp;

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
}
