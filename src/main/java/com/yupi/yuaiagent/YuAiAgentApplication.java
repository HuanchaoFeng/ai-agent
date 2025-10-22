package com.yupi.yuaiagent;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.yupi.yuaiagent.demo.sdkUseAI.callWithMessage;

@SpringBootApplication
public class YuAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuAiAgentApplication.class, args);

//        // 调用ai大模型处理
//        try {
//            GenerationResult result = callWithMessage();
//            System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());
//        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
//            System.err.println("错误信息："+e.getMessage());
//            System.out.println("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
//        }

    }

}
