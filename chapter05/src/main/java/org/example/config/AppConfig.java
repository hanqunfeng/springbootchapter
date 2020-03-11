package org.example.config;/**
 * Created by hanqf on 2020/3/5 11:13.
 */


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author hanqf
 * @date 2020/3/5 11:13
 */
@Configuration()
@ComponentScan("org.example")
//启用AspectJAutoProxy
@EnableAspectJAutoProxy
public class AppConfig {
}
