package com.wangtao.producer.controller;

import brave.baggage.BaggageField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author wangtao
 * Created at 2026-01-10
 */
@Slf4j
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private Executor jdkExecutor;

    @GetMapping("/get")
    public Map<String, Object> get(@RequestHeader HttpHeaders httpHeaders) {
        httpHeaders.forEach((name, values) -> {
            if (name.startsWith("x-b3") || name.equals("userid") || name.equals("username")) {
                log.info("{}: {}", name, values);
            }
        });

        log.info("get: {}: {}", BaggageField.getByName("userId").getValue(), BaggageField.getByName("userName").getValue());
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("username", "zhangsan");
        map.put("age", 30);
        map.put("acessTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        taskExecutor.execute(() -> {
            String userId = BaggageField.getByName("userId").getValue();
            String userName = BaggageField.getByName("userName").getValue();
            log.info("taskExecutor execute: {}: {}", userId, userName);
        });
        jdkExecutor.execute(() -> {
            String userId = BaggageField.getByName("userId").getValue();
            String userName = BaggageField.getByName("userName").getValue();
            log.info("jdkExecutor execute: {}: {}", userId, userName);
        });
        return map;
    }
}
