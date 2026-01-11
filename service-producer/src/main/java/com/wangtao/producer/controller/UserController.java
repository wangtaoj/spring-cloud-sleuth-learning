package com.wangtao.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2026-01-10
 */
@Slf4j
@RequestMapping("/api/user")
@RestController
public class UserController {

    @GetMapping("/get")
    public Map<String, Object> get() {
        log.info("=============get=============");
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("username", "zhangsan");
        map.put("age", 30);
        map.put("acessTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return map;
    }
}
