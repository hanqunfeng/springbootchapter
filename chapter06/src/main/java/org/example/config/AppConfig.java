package org.example.config;/**
 * Created by hanqf on 2020/3/5 16:13.
 */


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author hanqf
 * @date 2020/3/5 16:13
 */
@Configuration
@ComponentScan("org.example")
//启用注解事务管理，这里使用proxyTargetClass = true 表示全部启用CGLib代理，一般都是为service层增加事务注解，如果service没有定义接口JDK代理就不会起作用了
//这里可以不指定，spring会自动判断启用何种代理
@EnableTransactionManagement(proxyTargetClass = true)
public class AppConfig {
}
