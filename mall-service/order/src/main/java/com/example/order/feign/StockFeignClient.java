package com.example.order.feign;

import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "stock-service", configuration = com.example.common.feign.FeignConfig.class)
public interface StockFeignClient {

    @PostMapping("/stock/increase")
    Result<Boolean> increaseStock(@RequestBody Map<String, Object> params);
}
