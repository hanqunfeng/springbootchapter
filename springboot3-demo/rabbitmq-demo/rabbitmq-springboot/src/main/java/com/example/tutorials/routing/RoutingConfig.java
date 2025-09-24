package com.example.tutorials.routing;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * routing 模式，direct
 * <p>
 * 订阅者基于 routing key 接收消息，direct 完全匹配
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class RoutingConfig {

    @Bean
    public Queue routingQueue1() {
        // 创建 quorum queue
        Map<String, Object> params = new HashMap<>();
        params.put("x-queue-type", "quorum");
        return new Queue(RabbitMQConstants.QUEUE_NAME_ROUTING_1, true, false, false, params);
    }

    @Bean
    public Queue routingQueue2() {
        // 创建 classic queue
        return new Queue(RabbitMQConstants.QUEUE_NAME_ROUTING_2);
    }


    @Bean
    public DirectExchange routingDirect() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_NAME_ROUTING);
    }

    @Bean
    public Binding routingBinding1() {
        return BindingBuilder.bind(routingQueue1()).to(routingDirect()).with(RabbitMQConstants.ROUTING_KEY_ROUTING_1);
    }

    @Bean
    public Binding routingBinding2() {
        return BindingBuilder.bind(routingQueue2()).to(routingDirect()).with(RabbitMQConstants.ROUTING_KEY_ROUTING_2);
    }

}
