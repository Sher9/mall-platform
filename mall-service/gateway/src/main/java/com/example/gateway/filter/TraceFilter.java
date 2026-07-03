package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 全局 TraceId 过滤器
 * 在请求头中注入 TraceId，贯穿全链路
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 获取或生成 TraceId
        String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }
        
        // 生成 SpanId
        String spanId = generateSpanId();

        // 将 TraceId 和 SpanId 注入请求头，传递给下游服务
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(TRACE_ID_HEADER, traceId)
                .header(SPAN_ID_HEADER, spanId)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        // 优先级最高，确保 TraceId 在所有过滤器之前设置
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private String generateSpanId() {
        return UUID.randomUUID().toString().substring(0, 16).toUpperCase();
    }
}
