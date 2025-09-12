package com.hanqf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hanqf.demo.mapper")
public class ShardingsphereDemo03Application {

    public static void main(String[] args) {
        SpringApplication.run(ShardingsphereDemo03Application.class, args);
    }

}
