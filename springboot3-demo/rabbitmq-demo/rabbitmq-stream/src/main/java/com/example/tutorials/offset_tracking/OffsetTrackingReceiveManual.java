package com.example.tutorials.offset_tracking;

/**
 * Stream 偏移量跟踪
 * Created by hanqf on 2025/9/23 15:38.
 *
 * 创建一个消费者，并手动记录偏移量
 */


import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OffsetTrackingReceiveManual {
    private static final String STREAM_NAME = "stream-offset-tracking-java";
    public static void main(String[] args) throws InterruptedException {
        try (Environment environment =
                     EnvironmentFactory.createDefaultEnvironmentBuilder().requestedHeartbeat(Duration.ofSeconds(5)).build()) {

            // 创建一个名为stream-offset-tracking-java的流
            environment.streamCreator().stream(STREAM_NAME).maxLengthBytes(ByteCapacity.GB(1)).create();

            // 如果消费者记录了偏移量，则从该偏移量开始消费
            OffsetSpecification offsetSpecification = OffsetSpecification.first();

            AtomicLong firstOffset = new AtomicLong(-1);
            AtomicLong lastOffset = new AtomicLong(0);
            AtomicLong messageCount = new AtomicLong(0);

            CountDownLatch consumedLatch = new CountDownLatch(1);
            // 创建一个消费者
            environment.consumerBuilder().stream(STREAM_NAME)
                    .offset(offsetSpecification) // 设置开始偏移量
                    .name("offset-tracking-tutorial") // 消费者名称，本示例中会存储 消息的偏移量，必须设置
                    .manualTrackingStrategy().builder() // 手动跟踪策略，与之对应的是 自动跟踪策略
                    //.autoTrackingStrategy().builder() // 自动跟踪策略，与之对应的是 手动跟踪策略
                    .messageHandler(
                            (ctx, msg) -> {
                                // 获取第一个消息的偏移量
                                if (firstOffset.compareAndSet(-1, ctx.offset())) {
                                    System.out.println("First message received.");
                                }
                                //  存储偏移量，每10条消息存储一次偏移量
                                if (messageCount.incrementAndGet() % 10 == 0) {
                                    ctx.storeOffset(); // 存储偏移量
                                }
                                // 获取消息 内容
                                String body = new String(msg.getBodyAsBinary(), UTF_8);
                                System.out.printf("Received message:%d, %s.%n",ctx.offset(), body);

                                // 判断是否是marker消息，本示例中是marker消息，表示所有消息已经接收完毕
                                if ("marker".equals(body)) {
                                    lastOffset.set(ctx.offset());
                                    ctx.storeOffset(); // 存储偏移量
                                    ctx.consumer().close();
                                    consumedLatch.countDown();
                                }
                            })
                    .build();
            System.out.println("Started consuming...");

            consumedLatch.await(60, TimeUnit.MINUTES);

            System.out.printf("Done consuming, first offset %d, last offset %d.%n",
                    firstOffset.get(), lastOffset.get());
        }
    }

}
