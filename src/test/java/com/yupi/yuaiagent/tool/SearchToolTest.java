package com.yupi.yuaiagent.tool;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchToolTest {

    @Value("${search-api.api-key}")
    private String apiKey;

    @Test
    public void testSearchWeb() {
        WebSearchTool webSearchTool = new WebSearchTool(apiKey);
        String query = "leetcode website";
        String answer = webSearchTool.searchWeb(query);
        Assertions.assertNotNull(answer);
    }
}
