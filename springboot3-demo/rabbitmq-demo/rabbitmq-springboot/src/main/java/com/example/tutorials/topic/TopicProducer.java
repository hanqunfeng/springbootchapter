package com.example.tutorials.topic;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:28.
 */

@Component
public class TopicProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Map<String, Object> send1(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME_TOPIC, RabbitMQConstants.ROUTING_KEY_TOPIC_1, message);
        return message;
    }

    public Map<String, Object> send2(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME_TOPIC, RabbitMQConstants.ROUTING_KEY_TOPIC_2, message);
        return message;
    }

}
