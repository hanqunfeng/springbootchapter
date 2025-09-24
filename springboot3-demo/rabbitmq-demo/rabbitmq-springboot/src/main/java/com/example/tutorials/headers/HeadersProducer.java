package com.example.tutorials.headers;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:28.
 */

@Component
public class HeadersProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Map<String, Object> send1(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        // any 匹配，all匹配，单值匹配，exists匹配
        Map<String, Object> HeaersCondMap = Map.of("name", "zhangsan", "age", 20);
        MessagePostProcessor messagePostProcessor = m -> {
            // 设置消息头
            m.getMessageProperties().setHeaders(HeaersCondMap);
            return m;
        };
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME_HEADER, "", message, messagePostProcessor);
        return message;
    }

    public Map<String, Object> send2(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        // any 匹配
        Map<String, Object> HeaersCondMap = Map.of("age", 20);
        MessagePostProcessor messagePostProcessor = m -> {
            // 设置消息头
            m.getMessageProperties().setHeaders(HeaersCondMap);
            return m;
        };
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME_HEADER, "", message, messagePostProcessor);
        return message;
    }

    public Map<String, Object> send3(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        // exists匹配
        Map<String, Object> HeaersCondMap = Map.of("age", 21);
        MessagePostProcessor messagePostProcessor = m -> {
            // 设置消息头
            m.getMessageProperties().setHeaders(HeaersCondMap);
            return m;
        };
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME_HEADER, "", message, messagePostProcessor);
        return message;
    }


}
