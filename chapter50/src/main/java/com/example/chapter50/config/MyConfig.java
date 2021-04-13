package com.example.chapter50.config;

import com.example.chapter50.support.MyPropertySourceFactory;
import com.example.chapter50.support.MyResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * <h1>配置类</h1>
 * Created by hanqf on 2021/4/12 16:35.
 */

@Configuration
@PropertySource(name = "my", value = "classpath*:application-my-${spring.profiles.active}.properties", factory = MyPropertySourceFactory.class)
public class MyConfig {

    /**
     * 创建国际化资源处理器
    */
    @Bean("messageSource")
    public MessageSource messaeSource() {
        MyResourceBundleMessageSource messageSource = new MyResourceBundleMessageSource();
        messageSource.setPackagename("config.i18n");
        messageSource.setDefaultEncoding("utf-8");
        return messageSource;
    }
}
