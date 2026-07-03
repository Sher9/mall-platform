
package com.example.system.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", configuration = com.example.common.feign.FeignConfig.class)
public interface AuthFeignClient {

    @GetMapping("/user/info")
    String getUserInfo(@RequestParam("username") String username);
}
