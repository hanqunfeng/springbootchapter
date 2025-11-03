package com.example.consumer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * SimpleConsumer消费消息示例:异步订阅
 * Created by hanqf on 2025/10/25 22:45.
 */


public class AsyncSimpleConsumerExample {
    private static final Logger log = LoggerFactory.getLogger(AsyncSimpleConsumerExample.class);

    // 控制循环是否继续的标志
    static volatile boolean running = true;

    private AsyncSimpleConsumerExample() {
    }

    @SuppressWarnings({"resource", "InfiniteLoopStatement"})
    public static void main(String[] args) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        String endpoints = "68.79.47.19:8081";
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder().setEndpoints(endpoints).build();
        String consumerGroup = "yourConsumerGroup";
        Duration awaitDuration = Duration.ofSeconds(30);
        String tag = "*";
        String topic = "TestTopic";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);

        SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setConsumerGroup(consumerGroup)
                .setAwaitDuration(awaitDuration)
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .build();

        int maxMessageNum = 16;
        Duration invisibleDuration = Duration.ofSeconds(15);
        ExecutorService receiveCallbackExecutor = Executors.newCachedThreadPool();
        ExecutorService ackCallbackExecutor = Executors.newCachedThreadPool();



        // 添加 JVM 关闭钩子，用于优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down consumer...");
            running = false;
            try {
                consumer.close();
            } catch (Exception e) {
                log.error("Failed to close consumer", e);
            }
            receiveCallbackExecutor.shutdown();
            ackCallbackExecutor.shutdown();
        }));

        do {
            if (!running) break; // 检查运行状态

            final CompletableFuture<List<MessageView>> future0 = consumer.receiveAsync(maxMessageNum, invisibleDuration);

            future0.whenCompleteAsync((messages, throwable) -> {
                if (null != throwable) {
                    log.error("Failed to receive message from remote", throwable);
                    return;
                }
                log.info("Received {} message(s)", messages.size());

                final Map<MessageView, CompletableFuture<Void>> map =
                        messages.stream().collect(Collectors.toMap(message -> message, consumer::ackAsync));

                for (Map.Entry<MessageView, CompletableFuture<Void>> entry : map.entrySet()) {
                    final MessageId messageId = entry.getKey().getMessageId();

                    final CompletableFuture<Void> future = entry.getValue();
                    future.whenCompleteAsync((v, t) -> {
                        if (null != t) {
                            log.error("Message is failed to be acknowledged, messageId={}", messageId, t);
                            return;
                        }
                        log.info("Message is acknowledged successfully, messageId={}", messageId);
                        ByteBuffer bodyBuffer = entry.getKey().getBody();
                        // remaining() 获取可读字节数
                        byte[] bytes = new byte[bodyBuffer.remaining()];
                        // 读取指定的字节数
                        bodyBuffer.get(bytes);
                        // 如果发送端用了 MessageBuilder.setBody("hello".getBytes(StandardCharsets.UTF_8))，这才是字符串；
                        String msg = new String(bytes, StandardCharsets.UTF_8);
                        log.info("message={}", msg);

                    }, ackCallbackExecutor);
                }

            }, receiveCallbackExecutor);

            // 避免过于频繁拉取
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.warn("Consumer loop interrupted");
                break;
            }
        } while (running); // 使用变量控制退出

        log.info("Consumer stopped.");
    }

}
