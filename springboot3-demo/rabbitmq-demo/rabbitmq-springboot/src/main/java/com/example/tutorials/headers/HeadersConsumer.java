package com.example.tutorials.headers;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:22.
 */

@Component
public class HeadersConsumer {
    // 这里不声明 messageConverter 也可以，会自动使用 RabbitMQConfig 中声明的 messageConverter
    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_HEADER_1,
            RabbitMQConstants.QUEUE_NAME_HEADER_2,
            RabbitMQConstants.QUEUE_NAME_HEADER_3,
            RabbitMQConstants.QUEUE_NAME_HEADER_4})
    public void receive(Map<String, Object> message, @Header(AmqpHeaders.CONSUMER_QUEUE) String queueName, @Headers Map<String, Object> headers) {
        System.out.println("consumer received `" + queueName + "` message map: " + message);
        headers.forEach((key, value) -> {
            System.out.println("key: " + key + ", value: " + value);
        });
    }


}
