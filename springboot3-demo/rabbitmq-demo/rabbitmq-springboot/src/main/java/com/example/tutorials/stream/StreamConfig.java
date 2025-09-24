package com.example.tutorials.stream;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * hello world 模式，不需要 Exchange
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class StreamConfig {

    // stream 队列
    @Bean
    public Queue streamQueue() {
        Map<String,Object> params = new HashMap<>();
        params.put("x-queue-type","stream");
        params.put("x-max-length-bytes", 20_000_000_000L); // maximum stream size: 20 GB
        params.put("x-stream-max-segment-size-bytes", 100_000_000); // size of segment files: 100 MB

        return new Queue(RabbitMQConstants.QUEUE_NAME_STREAM,true,false,false,params);
    }
}
