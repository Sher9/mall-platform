package com.example.order.feign;

import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "product-service", configuration = com.example.common.feign.FeignConfig.class)
public interface ProductFeignClient {

    @GetMapping("/{productId}")
    Result<Map<String, Object>> getById(@PathVariable("productId") Long productId);
}
