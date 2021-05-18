package com.example.jarwarweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
public class JarWarWebApplication extends SpringBootServletInitializer {

    /**
     * jar方式启动的处理方法，pom中打包方式要修改为jar
     *
     * @param args String
     */
    public static void main(String[] args) {
        configureApplication(new SpringApplicationBuilder()).run(args);
    }

    /**
     * war方式启动和jar方式启动共用的配置
     *
     * @param builder SpringApplicationBuilder
     */
    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {

        return builder.sources(JarWarWebApplication.class)
                // 在应用环境准备好后执行（此时application.properties和PoropertySource已读取），
                // 但是此时BeanFactory还未准备好（也就是Bean还未创建）
                .listeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
                    SpringApplication springApplication = event.getSpringApplication();

                    //获取配置和环境信息，就是看着玩的
                    ConfigurableEnvironment env = event.getEnvironment();
                    System.out.println("=====MutablePropertySources start========");
                    MutablePropertySources propertySources = env.getPropertySources();
                    propertySources.stream().forEach(System.out::println);
                    System.out.println("=====MutablePropertySources end========");

                    System.out.println("=====activeProfiles start========");
                    String[] activeProfiles = env.getActiveProfiles();
                    Arrays.stream(activeProfiles).iterator().forEachRemaining(System.out::println);
                    System.out.println("=====activeProfiles end========");

                    System.out.println("=====systemProperties start========");
                    Map<String, Object> systemProperties = env.getSystemProperties();
                    systemProperties.forEach((k, v) -> System.out.println(k + ":" + v));
                    System.out.println("=====systemProperties end========");

                    System.out.println("=====systemEnvironment start========");
                    Map<String, Object> systemEnvironment = env.getSystemEnvironment();
                    systemEnvironment.forEach((k, v) -> System.out.println(k + ":" + v));
                    System.out.println("=====systemEnvironment end========");
                });
    }

    /**
     * war方式启动的处理方法，pom中打包方式要修改为war
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

}
