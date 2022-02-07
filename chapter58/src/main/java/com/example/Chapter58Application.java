package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
//开启重试
@EnableRetry
public class Chapter58Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter58Application.class, args);
    }

}
