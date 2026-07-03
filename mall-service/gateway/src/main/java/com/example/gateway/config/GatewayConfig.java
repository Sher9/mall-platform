package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Configuration
public class GatewayConfig {

    /**
     * 基于 IP 的限流 Key 解析器（默认优先使用）
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                   .getAddress()
                   .getHostAddress()
        );
    }

    /**
     * 基于用户 ID 的限流 Key 解析器（从请求头获取）
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getHeaders().getFirst("X-User-Id") 
            != null ? exchange.getRequest().getHeaders().getFirst("X-User-Id") 
                    : UUID.randomUUID().toString()
        );
    }

    /**
     * 基于接口路径的限流 Key 解析器
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getPath().value()
        );
    }
}
