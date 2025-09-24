package com.example.tutorials.work_queues;

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
public class WorkQueuesProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Map<String, Object> send(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        rabbitTemplate.convertAndSend(RabbitMQConstants.QUEUE_NAME_WORK_QUEUES, message);
        return message;
    }

}
