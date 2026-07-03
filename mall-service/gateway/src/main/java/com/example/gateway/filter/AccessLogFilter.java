package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 全局访问日志过滤器
 * 记录所有请求的访问日志
 */
@Slf4j
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        
        String traceId = request.getHeaders().getFirst("X-Trace-Id");
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String query = request.getQueryParams().toString();
        String remoteAddr = getRemoteAddress(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();
            int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 500;
            
            // 记录访问日志
            log.info("[ACCESS] {} | {} | {} | {} | {} | {}ms | {} | {}",
                    LocalDateTime.now().format(FORMATTER),
                    traceId,
                    method,
                    path + (query != null && !query.isEmpty() ? "?" + query.replace("MultiValueMap", "") : ""),
                    remoteAddr,
                    duration,
                    statusCode,
                    userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 100)) : "");
        }));
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private String getRemoteAddress(ServerHttpRequest request) {
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        // 从 X-Forwarded-For 头获取真实 IP（经过代理）
        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return "unknown";
    }
}
