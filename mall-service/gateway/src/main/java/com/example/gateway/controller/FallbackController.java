
package com.example.gateway.controller;

import com.example.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Result<String> fallbackGet() {
        return Result.error(503, "服务暂时不可用，请稍后重试");
    }

    @PostMapping("/fallback")
    public Result<String> fallbackPost() {
        return Result.error(503, "服务暂时不可用，请稍后重试");
    }
}
