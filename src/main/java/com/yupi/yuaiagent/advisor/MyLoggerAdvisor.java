package com.yupi.yuaiagent.advisor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private AdvisedRequest before(AdvisedRequest advisedRequest){
        log.info("AI Request:{}",advisedRequest.userText());
        return advisedRequest;
    }

    private void  after(AdvisedResponse advisedResponse){
        log.info("AI Response:{}",advisedResponse.response().getResult().getOutput().getText());
    }

    //非流式处理
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        /**
         * 体现了 “拦截器/装饰器模式（Interceptor / AOP Advisor Pattern）” 的典型结构：
         * 每个拦截器既可以读取，也可以修改请求对象。
         */
        advisedRequest = this.before(advisedRequest); //拦截请求
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest); // 放行
        this.after(advisedResponse); // 后置响应处理
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

        /**
         * 流式打印，在最后使用一个聚合器把最终的结果收集起来（如果不做，则每次打印都是每一块的输出
         */
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);
        return new MessageAggregator().aggregateAdvisedResponse(advisedResponseFlux, this::after);

    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0; //指定该advisor的优先级
    }
}
