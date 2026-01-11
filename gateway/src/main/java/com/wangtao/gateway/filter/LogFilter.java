package com.wangtao.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author wangtao
 * Created at 2024-09-22
 */
@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getURI().getPath();
        log.info("request url: {}", requestUrl);
        return chain.filter(exchange);
    }

    /**
     * 必须放在ReactiveLoadBalancerClientFilter之前执行
     * 从nacos注册中心获取实例时会切换线程到boundedElastic-1
     * 导致traceContext丢失
     */
    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER - 1;
    }
}
