package com.yupi.yuaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.converter.StructuredOutputConverter;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@Slf4j
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    public AdvisedRequest before(AdvisedRequest advisedRequest) {
        /**
         * 改写用户 Prompt为：
         *  {Input_Query}
         *  Read the question again: {Input_Query}
         *  可以用直接拼接的方式，也可以用下面的先把userParams参数填充，再覆盖userText
         */

        HashMap<String, Object> map = new HashMap<>(advisedRequest.userParams());
        map.put("re2_input_query", advisedRequest.userText());
        AdvisedRequest advisedRequest1 = AdvisedRequest.from(advisedRequest).userText("""
                {re2_input_query}
                read the question again:{re2_input_query}
                """).userParams(map).build();
        return  advisedRequest1;

    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);
        return advisedResponseFlux;
    }

    @Override
    public String getName() {
        return ReReadingAdvisor.class.getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
