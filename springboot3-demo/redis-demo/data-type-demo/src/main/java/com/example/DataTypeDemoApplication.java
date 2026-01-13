package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DataTypeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataTypeDemoApplication.class, args);
    }

}
