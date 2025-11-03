package com.example.consumer;

import org.apache.rocketmq.client.apis.*;
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

/**
 * https://github.com/apache/rocketmq-clients/blob/master/java/client/src/main/java/org/apache/rocketmq/client/java/example/SimpleConsumerExample.java
 * SimpleConsumer消费消息示例:同步订阅
 * Created by hanqf on 2025/10/25 22:31.
 */


public class SimpleConsumerExample {
    private static final Logger log = LoggerFactory.getLogger(SimpleConsumerExample.class);

    private SimpleConsumerExample() {
    }

    @SuppressWarnings({"resource", "InfiniteLoopStatement"})
    public static void main(String[] args) throws ClientException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

//        // Credential provider is optional for client configuration.
        String accessKey = "mqadmin";
        String secretKey = "1234567";
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(accessKey, secretKey);
        // proxy地址，多个proxy地址用分号隔开，因为proxy为无状态服务，所以可以通过nginx进行负载均衡
//        String endpoints = "68.79.47.19:8081";
        String endpoints = "127.0.0.1:8081";
//        String endpoints = "52.83.24.68:8081";
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
                // client configuration to solve the problem please if SSL is not essential.
                // .enableSsl(false)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        String consumerGroup = "yourConsumerGroup";
        Duration awaitDuration = Duration.ofSeconds(30);
//        String tag = "yourMessageTagA";
        String tag = "*";
        String topic = "TestTopic";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        // In most case, you don't need to create too many consumers, singleton pattern is recommended.
        SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the consumer group name.
                .setConsumerGroup(consumerGroup)
                // set await duration for long-polling.
                .setAwaitDuration(awaitDuration)
                // Set the subscription for the consumer.
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .build();
        // Max message num for each long polling.
        int maxMessageNum = 16;
        // Set message invisible duration after it is received.
        Duration invisibleDuration = Duration.ofSeconds(15);
        // Receive message, multi-threading is more recommended.
        do {
            Thread.sleep(1000);
            final List<MessageView> messages = consumer.receive(maxMessageNum, invisibleDuration);
            log.info("Received {} message(s)", messages.size());
            for (MessageView message : messages) {
                final MessageId messageId = message.getMessageId();
                try {
                    consumer.ack(message);
                    log.info("Message is acknowledged successfully, messageId={}", messageId);
                    ByteBuffer bodyBuffer = message.getBody();
                    // remaining() 获取可读字节数
                    byte[] bytes = new byte[bodyBuffer.remaining()];
                    // 读取指定的字节数
                    bodyBuffer.get(bytes);
                    // 如果发送端用了 MessageBuilder.setBody("hello".getBytes(StandardCharsets.UTF_8))，这才是字符串；
                    String msg = new String(bytes, StandardCharsets.UTF_8);

                    log.info("message={}", msg);
                } catch (Throwable t) {
                    log.error("Message is failed to be acknowledged, messageId={}", messageId, t);
                }
            }
        } while (true);
        // Close the simple consumer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // consumer.close();
    }
}
