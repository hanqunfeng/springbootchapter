package com.example.consumer;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * PushConsumer消费消息示例
 * Created by hanqf on 2025/10/25 20:36.
 */


public class PushConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(PushConsumerExample.class);

    private PushConsumerExample() {
    }

    public static void main(String[] args) throws ClientException, IOException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        String accessKey = "mqadmin";
        String secretKey = "1234567";
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(accessKey, secretKey);
        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8081;xxx:8081。
//        String endpoints = "68.79.47.19:8081";
//        String endpoints = "52.83.24.68:8081";
        String endpoints = "127.0.0.1:8081";
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        // 订阅消息的过滤规则，表示订阅所有Tag的消息。
        String tag = "*";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        // 为消费者指定所属的消费者分组，Group需要提前创建。
        String consumerGroup = "yourConsumerGroup";

        // -o 顺序消息
//        ./bin/mqadmin updateSubGroup -c DefaultCluster -g FIFOGroup -n 127.0.0.1:9876 -o true
//        String consumerGroup = "FIFOGroup";

        // -d 广播消息，不过广播消息目前不好使啊
//        ./bin/mqadmin updateSubGroup -c DefaultCluster -g broadcastGroup -n 127.0.0.1:9876 -d true
//        String consumerGroup = "broadcastGroup";

        // 指定需要订阅哪个目标Topic，Topic需要提前创建。
        String topic = "TestTopic";
        // 初始化PushConsumer，需要绑定消费者分组ConsumerGroup、通信参数以及订阅关系。
        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // 设置消费者分组。
                .setConsumerGroup(consumerGroup)
                // 设置预绑定的订阅关系。
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                // 设置消费监听器。
                .setMessageListener(messageView -> {
                    // 处理消息并返回消费结果。
                    logger.info("Consume message successfully, messageId={}", messageView.getMessageId());

                    ByteBuffer bodyBuffer = messageView.getBody();
                    // remaining() 获取可读字节数
                    byte[] bytes = new byte[bodyBuffer.remaining()];
                    // 读取指定的字节数
                    bodyBuffer.get(bytes);
                    // 如果发送端用了 MessageBuilder.setBody("hello".getBytes(StandardCharsets.UTF_8))，这才是字符串；
                    String msg = new String(bytes, StandardCharsets.UTF_8);

                    logger.info("message={}", msg);
                    return ConsumeResult.SUCCESS;
                })
                .build();
        Thread.sleep(Long.MAX_VALUE);
        // 如果不需要再使用 PushConsumer，可关闭该实例。
        // pushConsumer.close();
    }
}
