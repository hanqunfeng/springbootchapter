package org.example.config;/**
 * Created by hanqf on 2020/3/2 10:51.
 */


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author hanqf
 * @date 2020/3/2 10:51
 */
@Configuration
@ImportResource(value = {"classpath:spring-other.xml"})
public class OtherConfig {
}
