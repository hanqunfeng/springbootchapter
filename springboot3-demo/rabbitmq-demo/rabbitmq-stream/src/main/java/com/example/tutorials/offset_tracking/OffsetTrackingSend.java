package com.example.tutorials.offset_tracking;

/**
 * Stream 偏移量跟踪
 * Created by hanqf on 2025/9/23 15:38.
 */


import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OffsetTrackingSend {
    private static final String STREAM_NAME = "stream-offset-tracking-java";
    public static void main(String[] args) throws InterruptedException {
        try (Environment environment = EnvironmentFactory.createDefaultEnvironment()) {
            // 创建一个名为"stream-offset-tracking-java"的流
            environment.streamCreator().stream(STREAM_NAME).maxLengthBytes(ByteCapacity.GB(1)).create();
            // 创建一个生产者
            Producer producer = environment.producerBuilder().stream(STREAM_NAME).build();

            int messageCount = 100;
            CountDownLatch confirmedLatch = new CountDownLatch(messageCount);
            System.out.printf("Publishing %d messages...%n", messageCount);

            IntStream.range(0, messageCount).forEach(i -> {
                // 创建消息,最后一条消息是 "marker"
                String body = i == messageCount - 1 ? "marker" : "hello";
                System.out.printf("Publishing message %d with body %s.%n", i, body);
                // 发送消息
                producer.send(producer.messageBuilder().addData(body.getBytes(UTF_8)).build(),
                        ctx -> {
                            if (ctx.isConfirmed()) {
                                confirmedLatch.countDown();
                            }
                        });
            });

            boolean completed = confirmedLatch.await(60, TimeUnit.SECONDS);
            System.out.printf("Messages confirmed: %b.%n", completed);
        }
    }
}
