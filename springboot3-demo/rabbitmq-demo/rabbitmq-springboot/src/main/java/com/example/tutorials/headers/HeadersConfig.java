package com.example.tutorials.headers;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * header 模式
 *
 * 基于消息 header 进行匹配
 *
 * 效率较低，官方不推荐使用
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class HeadersConfig {

    // header 绑定键名称
    public static final Map<String,Object> HeaersCondMap = Map.of("name", "zhangsan", "age", 20);


    @Bean
    public Queue headersQueue1() {
        // 创建 quorum queue
        Map<String,Object> params = new HashMap<>();
        params.put("x-queue-type","quorum");
        return new Queue(RabbitMQConstants.QUEUE_NAME_HEADER_1, true, false, false, params);
    }

    @Bean
    public Queue headersQueue2() {
        // 创建 classic queue
        return new Queue(RabbitMQConstants.QUEUE_NAME_HEADER_2);
    }

    @Bean
    public Queue headersQueue3() {
        // 创建 classic queue
        return new Queue(RabbitMQConstants.QUEUE_NAME_HEADER_3);
    }

    @Bean
    public Queue headersQueue4() {
        // 创建 classic queue
        return new Queue(RabbitMQConstants.QUEUE_NAME_HEADER_4);
    }


    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(RabbitMQConstants.EXCHANGE_NAME_HEADER);
    }

    @Bean
    public Binding headersBinding1() {
        // any 匹配，任意一个匹配成功都可以发送到 该队列
        return BindingBuilder.bind(headersQueue1()).to(headersExchange()).whereAny(HeaersCondMap).match();
    }

    @Bean
    public Binding headersBinding2() {
        // all 匹配，必须全部匹配成功才能发送到该队列
        return BindingBuilder.bind(headersQueue2()).to(headersExchange()).whereAll(HeaersCondMap).match();
    }


    @Bean
    public Binding headersBinding3() {
        // 单个值匹配
        return BindingBuilder.bind(headersQueue3()).to(headersExchange()).where("name").matches("zhangsan");
    }

    @Bean
    public Binding headersBinding4() {
        // 单个key存在
        return BindingBuilder.bind(headersQueue4()).to(headersExchange()).where("age").exists();
    }

}
