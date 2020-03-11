package com.example;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.mapper.autoconfigure.MapperAutoConfiguration;

@SpringBootApplication(exclude = {PageHelperAutoConfiguration.class, MapperAutoConfiguration.class})
public class Chapter14Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter14Application.class, args);
    }

}
