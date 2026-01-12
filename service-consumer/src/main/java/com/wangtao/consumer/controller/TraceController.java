package com.wangtao.consumer.controller;

import brave.baggage.BaggageField;
import com.wangtao.consumer.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wangtao
 * Created at 2026-01-10
 */
@Slf4j
@RequestMapping("/api")
@RestController
public class TraceController {

    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping("/trace")
    public Map<String, Object> trace() {
        log.info("=============trace=============");
        // 往下游传递userId
        BaggageField.getByName("userId").updateValue("123456");
        // 往下游传递userName
        BaggageField.getByName("userName").updateValue("zhangsan");
        return userFeignClient.get();
    }
}
