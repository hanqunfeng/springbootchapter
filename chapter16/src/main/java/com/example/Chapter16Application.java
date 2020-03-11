package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {MybatisAutoConfiguration.class})
@SpringBootApplication
public class Chapter16Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter16Application.class, args);
    }

}
