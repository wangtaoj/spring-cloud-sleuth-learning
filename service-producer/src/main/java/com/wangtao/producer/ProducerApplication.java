package com.wangtao.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author wangtao
 * Created at 2023/6/10 15:29
 */
@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Bean
    public TaskExecutor taskExecutor(TaskExecutorBuilder builder) {
        return builder.build();
    }

    @Bean
    public Executor jdkExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
