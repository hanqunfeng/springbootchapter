package com.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 *
 * Created by hanqf on 2025/10/15 17:58.
 */


@Component
public class KafkaConsumer {
    public static final String GROUP_ID = "test";

    // 消费监听
    @KafkaListener(topics = {KafkaProducer.TOPIC_NAME}, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<?, ?> record) {
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("简单消费：" + record.topic() + "-" + record.partition() + "-" + record.value());
    }
}
