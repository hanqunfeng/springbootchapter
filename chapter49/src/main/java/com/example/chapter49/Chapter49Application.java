package com.example.chapter49;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Map;

@SpringBootApplication
public class Chapter49Application {

    //springboot的启动方式
    public static void main(String[] args) {
        // 1
        //SpringApplication.run(Chapter49Application.class, args);

        // 2
        SpringApplication springApplication = new SpringApplication(Chapter49Application.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        ConfigurableEnvironment environment = springApplication.run(args).getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        //PropertySource<?> propertySource = propertySources.get("Config resource 'classpath:/application.properties' via location 'optional:classpath:/'");
        PropertySource<?> propertySource = propertySources.get("Config resource 'classpath:/application.yml' via location 'optional:classpath:/'");
        Object a = propertySource.getProperty("a");
        System.out.println(a);

        Object source = propertySource.getSource();

        if(source instanceof Map){
            ((Map)source).forEach((key,val) -> {
                System.out.println(key + "=" +val);
            });
        }

        // 3
        //new SpringApplicationBuilder()
        //        .sources(Chapter49Application.class)
        //        .bannerMode(Banner.Mode.OFF)
        //        .run(args);



    }

}
