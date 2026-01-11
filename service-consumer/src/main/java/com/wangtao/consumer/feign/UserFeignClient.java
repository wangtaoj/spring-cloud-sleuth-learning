package com.wangtao.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author wangtao
 * Created at 2026-01-10
 */
@FeignClient(value=UserFeignClient.SERVICE_NAME, path = "/api/user")
public interface UserFeignClient {

    String SERVICE_NAME = "service-producer";

    @GetMapping("/get")
    Map<String, Object> get();

}
