package com.example.tutorials.work_queues;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * work queues 模式，不需要 Exchange
 *
 * 消息会尽可能均匀的分配给消费者
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class WorkQueuesConfig {

    @Bean
    public Queue workQueuesQueue() {
        // 创建 quorum queue
        Map<String,Object> params = new HashMap<>();
        params.put("x-queue-type","quorum");
        return new Queue(RabbitMQConstants.QUEUE_NAME_WORK_QUEUES, true, false, false, params);
    }

}
