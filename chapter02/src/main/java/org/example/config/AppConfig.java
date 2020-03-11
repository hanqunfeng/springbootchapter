package org.example.config;/**
 * Created by hanqf on 2020/3/2 11:29.
 */


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanqf
 * @date 2020/3/2 11:29
 */
@Configuration
//lazyInit = true 延迟注入
//@ComponentScan(value = "org.example",lazyInit = true)
@ComponentScan(value = "org.example")
public class AppConfig {
}
