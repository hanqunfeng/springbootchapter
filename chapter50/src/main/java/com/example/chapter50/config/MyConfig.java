package com.example.chapter50.config;

import com.example.chapter50.support.MyPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * <h1></h1>
 * Created by hanqf on 2021/4/12 16:35.
 */

@Configuration
@PropertySource(name = "my", value = "classpath*:application-my-${spring.profiles.active}.properties", factory = MyPropertySourceFactory.class)
public class MyConfig {
}
