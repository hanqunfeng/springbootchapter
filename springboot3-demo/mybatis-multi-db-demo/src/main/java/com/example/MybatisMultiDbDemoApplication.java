package com.example;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//关闭分页插件的自动配置
@SpringBootApplication(exclude = {PageHelperAutoConfiguration.class})
public class MybatisMultiDbDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisMultiDbDemoApplication.class, args);
    }

}
