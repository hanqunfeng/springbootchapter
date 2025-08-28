package com.hanqf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hanqf.demo.mapper")
public class ShardsphereDemo01Application {

    public static void main(String[] args) {
        SpringApplication.run(ShardsphereDemo01Application.class, args);
    }

}
