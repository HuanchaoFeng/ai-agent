package com.yupi.yuaiagent.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ResourceDownloadToolTest {


    @Test
    public void resourceDownloadToolTest(){
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        String url = "https://www.codefather.cn/logo.png";
        String fileName = "logo.png";
        String result = resourceDownloadTool.downloadResourceFromUrl(url,fileName);
        Assertions.assertNotNull(result);

    }
}
