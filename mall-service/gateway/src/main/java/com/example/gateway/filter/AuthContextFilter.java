package com.example.gateway.filter;

import com.example.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用户上下文过滤器
 * 解析 Token 中的用户信息，注入请求头透传给下游服务
 */
@Slf4j
@Component
public class AuthContextFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    private static final String USER_TYPE_HEADER = "X-User-Type";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = extractToken(request);
        
        if (StringUtils.hasText(token)) {
            try {
                // 从 Token 中解析用户信息
                String username = jwtUtil.getUsernameFromToken(token);
                
                // 判断用户类型：admin 还是 customer
                String userType = determineUserType(request.getPath().value());
                
                // 将用户信息注入请求头
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(USER_NAME_HEADER, username)
                        .header(USER_TYPE_HEADER, userType)
                        .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                log.warn("Failed to parse token: {}", e.getMessage());
            }
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 在 AuthenticationFilter 之后执行
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String determineUserType(String path) {
        if (path.startsWith("/api/system") || path.startsWith("/admin")) {
            return "ADMIN";
        } else if (path.startsWith("/api/customer") || path.startsWith("/customer")) {
            return "CUSTOMER";
        }
        return "UNKNOWN";
    }
}
