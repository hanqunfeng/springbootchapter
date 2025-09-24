package com.example.tutorials.hello_world;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hello world 模式，不需要 Exchange
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class HelloWorldConfig {

    @Bean
    public Queue helloWorldQueueString() {
        return new Queue(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD);
    }

    @Bean
    public Queue helloWorldQueueMap() {
        return new Queue(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD_MAP);
    }

}
