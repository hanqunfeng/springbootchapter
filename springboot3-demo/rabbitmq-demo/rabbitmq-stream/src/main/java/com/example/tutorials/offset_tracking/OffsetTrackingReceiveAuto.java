package com.example.tutorials.offset_tracking;

/**
 * Stream 偏移量跟踪
 * Created by hanqf on 2025/9/23 15:38.
 *
 * 创建一个消费者，并自动跟踪偏移量
 */


import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OffsetTrackingReceiveAuto {
    private static final String STREAM_NAME = "stream-offset-tracking-java";

    public static void main(String[] args) throws IOException {
        try (Environment environment =
                     EnvironmentFactory.createDefaultEnvironmentBuilder().requestedHeartbeat(Duration.ofSeconds(5)).build()) {

            // 创建一个名为stream-offset-tracking-java的流
            environment.streamCreator().stream(STREAM_NAME).maxLengthBytes(ByteCapacity.GB(1)).create();

            // 如果消费者记录了偏移量，则从该偏移量开始消费
            OffsetSpecification offsetSpecification = OffsetSpecification.first();

            AtomicLong firstOffset = new AtomicLong(-1);
            AtomicLong lastOffset = new AtomicLong(0);

            // 创建一个消费者
            Consumer consumer = environment.consumerBuilder().stream(STREAM_NAME)
                    .offset(offsetSpecification) // 设置开始偏移量
                    .name("offset-tracking-tutorial-auto") // 消费者名称，本示例中会存储 消息的偏移量，必须设置
//                    .manualTrackingStrategy().builder() // 手动跟踪策略，与之对应的是 自动跟踪策略
                    .autoTrackingStrategy().builder() // 自动跟踪策略，与之对应的是 手动跟踪策略
                    .messageHandler(
                            (ctx, msg) -> {
                                long currentOffset = ctx.offset();
                                // 获取第一个消息的偏移量
                                if (firstOffset.compareAndSet(-1, currentOffset)) {
                                    System.out.println("First message received.");
                                }

                                // 获取消息 内容
                                String body = new String(msg.getBodyAsBinary(), UTF_8);
                                System.out.printf("Received message:%d, %s.%n", currentOffset, body);

                                lastOffset.set(currentOffset);
                            })
                    .build();
            System.out.println("Started consuming...");

            System.in.read();
            consumer.close();

            System.out.printf("Done consuming, first offset %d, last offset %d.%n",
                    firstOffset.get(), lastOffset.get());
        }
    }

}
