package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.dao")
public class ClickhouseMybatisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseMybatisDemoApplication.class, args);
    }

}
