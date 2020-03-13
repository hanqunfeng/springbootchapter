package com.example.config;/**
 * Created by hanqf on 2020/3/8 15:45.
 */


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author hanqf
 * @date 2020/3/8 15:45
 */
@Configuration
//启用注解事务管理，这里使用proxyTargetClass = true 表示全部启用CGLib代理，一般都是为service层增加事务注解，如果service没有定义接口JDK代理就不会起作用了
//这里可以不指定，spring会自动判断启用何种代理
@EnableTransactionManagement(proxyTargetClass = true)
//定义JPA接口扫描包路径，可以不声明，springboot会自动查找
@EnableJpaRepositories(basePackages = "com.example.dao")
//定义实体Bean扫描包路径，可以不声明，springboot会自动查找
@EntityScan(basePackages = "com.example.model")
public class AppConfig {


}
