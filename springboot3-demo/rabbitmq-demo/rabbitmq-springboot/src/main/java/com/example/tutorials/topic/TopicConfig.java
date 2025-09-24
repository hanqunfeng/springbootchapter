package com.example.tutorials.topic;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * topic 模式
 * <p>
 * 订阅者基于 routing key 接收消息，topic 模糊匹配
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class TopicConfig {

    @Bean
    public Queue topicQueue1() {
        // 创建 quorum queue
        Map<String, Object> params = new HashMap<>();
        params.put("x-queue-type", "quorum");
        return new Queue(RabbitMQConstants.QUEUE_NAME_TOPIC_1, true, false, false, params);
    }

    @Bean
    public Queue topicQueue2() {
        // 创建 classic queue
        return new Queue(RabbitMQConstants.QUEUE_NAME_TOPIC_2);
    }


    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE_NAME_TOPIC);
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(RabbitMQConstants.ROUTING_KEY_TOPIC_P1);
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(RabbitMQConstants.ROUTING_KEY_TOPIC_P2);
    }

}
