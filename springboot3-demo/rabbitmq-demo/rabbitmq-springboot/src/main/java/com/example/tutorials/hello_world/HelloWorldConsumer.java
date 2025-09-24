package com.example.tutorials.hello_world;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:22.
 */

@Component
public class HelloWorldConsumer {
    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_HELLO_WORLD}, replyContentType = MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
    public void receive(String message) {
        System.out.println("consumer received message : " + message);
    }

    // 这里不声明 messageConverter 也可以，会自动使用 RabbitMQConfig 中声明的 messageConverter
    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_HELLO_WORLD_MAP}, messageConverter = "jsonConverter")
    public void receive(Map<String, Object> message) {
        System.out.println("consumer received message map: " + message);
    }

}
