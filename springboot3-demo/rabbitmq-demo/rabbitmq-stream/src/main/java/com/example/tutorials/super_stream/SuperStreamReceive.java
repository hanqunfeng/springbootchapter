package com.example.tutorials.super_stream;

import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.io.IOException;

/**
 *
 * Created by hanqf on 2025/9/26 18:31.
 */


public class SuperStreamReceive {
    public static final String superStreamName = "super_stream_invoices";
    public static void main(String[] args) throws InterruptedException, IOException {
        // 1. 创建 Environment（客户端的连接上下文）
        Environment environment = EnvironmentFactory.createDefaultEnvironment();

        int partitions = 3;

        // （可选）如果未事先创建 super stream 拓扑，则可以使用客户端 API 创建
        environment.streamCreator()
                .name(superStreamName)
                .superStream()
                .partitions(partitions)
                .creator()
                .create();

        // 3. 创建 Consumer，用于从 super stream 接收消息

        Consumer consumer = environment.consumerBuilder()
                .superStream(superStreamName)
                // 如果你希望多个应用实例之间使用 single active consumer 保证分区内顺序
                .name("consumer-group-1")
                .singleActiveConsumer()
                // 从最早的 offset 开始读（也可以根据业务选择 first/last/指定 offset）
                .offset(OffsetSpecification.first())
                .messageHandler((context, message) -> {
                    String body = new String(message.getBodyAsBinary());
                    System.out.println("Received: partition = " + context.stream()
                            + ", offset = " + context.offset()
                            + ", body = " + body);
                    // 手动存储 offset（如果使用手动 tracking 策略）
                    context.storeOffset();

                })
                .build();

        // 等待接收完毕
        System.in.read();
        // 4. 关闭资源
        consumer.close();
        environment.close();
    }
}
