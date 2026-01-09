package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamReadArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * Stream — Redisson 对象与实现解析
 * Created by hanqf on 2026/1/9 18:06.
 */

@SpringBootTest
public class StreamTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RStream<K,V> —— 标准消息流（Pull 模式）
     */
    @Test
    void testRStream() {
        RStream<String, String> stream =
                redisson.getStream("stream:order");

        // 添加消息
        StreamMessageId id = stream.add(StreamAddArgs.entries(
                "orderId", "1001",
                "status", "PAID"));
        System.out.println("msgId = " + id);

        // 从头开始读取
        StreamReadArgs args = StreamReadArgs.greaterThan(new StreamMessageId(0,0));

        Map<StreamMessageId, Map<String, String>> messages =
                stream.read(args);

        messages.forEach((messageId, body) -> {
            System.out.println(messageId + " -> " + body);
        });


        // 创建消费组（只需执行一次）
//        stream.createGroup(StreamCreateGroupArgs
//                .name("group1")
//                .id(new StreamMessageId(0,0))
//                .makeStream());

        // 消费
        Map<StreamMessageId, Map<String, String>> msgs =
                stream.readGroup(
                        "group1",
                        "consumer-1",
                        StreamReadGroupArgs.neverDelivered()
                );

        msgs.forEach((messageId, body) -> {
            System.out.println("process " + messageId);
            // ACK
            stream.ack("group1", messageId);
        });



    }


}
