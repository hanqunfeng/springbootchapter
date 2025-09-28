package com.example.tutorials.super_stream;

import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SuperStreamSend {
    public static final String superStreamName = "super_stream_invoices";

    public static void main(String[] args) throws Exception {
        // 1. 创建 Environment（客户端的连接上下文）
        Environment environment = EnvironmentFactory.createDefaultEnvironment(); // 集群连接方式无法连接

        int partitions = 3;

        // （可选）如果未事先创建 super stream 拓扑，则可以使用客户端 API 创建
        environment.streamCreator()
                .name(superStreamName)
                .superStream()
                .partitions(partitions)
                .creator()
                .create();

        // 2. 创建 Producer，用于向 super stream 发送消息
        Producer producer = environment.producerBuilder()
                .superStream(superStreamName)
                // 路由函数：从 message 中抽取用于分区的 key
                .routing(msg -> {
                    // 使用 messageId 或者消息体某个字段作为 routing key
                    return msg.getProperties().getMessageIdAsString();
                })
                // 也可以自定义 hash 函数：平均分配到每个分区
                .hash(routingKey -> routingKey.hashCode() % partitions)
                .producerBuilder()
                .build();

        int messageCount = 10;
        CountDownLatch confirmedLatch = new CountDownLatch(messageCount);
        // 发送几条消息作为示例
        for (int i = 0; i < messageCount; i++) {
            String msgBody = "Hello super-stream message " + i;
            System.out.println("Sending message: " + msgBody);

            // 构建消息体并发送
            Message message = producer.messageBuilder()
                    .properties()
                    .messageId(msgBody).messageBuilder()
                    .addData(msgBody.getBytes(UTF_8))
                    .build();

            producer.send(message, ctx -> {
                if (ctx.isConfirmed()) {
                    System.out.println("Message confirmed.");
                    confirmedLatch.countDown();
                }
            });
        }

        // 等待消息确认
        boolean completed = confirmedLatch.await(60, TimeUnit.SECONDS);
        System.out.printf("Messages confirmed: %b.%n", completed);

        producer.close();
        environment.close();
    }
}
