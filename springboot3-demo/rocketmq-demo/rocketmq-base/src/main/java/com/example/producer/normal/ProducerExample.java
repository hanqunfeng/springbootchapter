package com.example.producer.normal;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 发送消息示例:同步发送
 * Created by hanqf on 2025/10/25 20:25.
 */


public class ProducerExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerExample.class);

    public static void main(String[] args) throws ClientException {
        // 1.获取客户端服务提供者
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        String accessKey = "mqadmin";
        String secretKey = "1234567";
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(accessKey, secretKey);

        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081。多个接入点之间用;分隔。
//        String endpoint = "68.79.47.19:8081";
//        String endpoint = "52.83.24.68:9081";
//        String endpoint = "69.234.254.139:8081";
//        String endpoint = "69.234.254.139:8081;69.234.254.139:9081;52.83.24.68:8081;52.83.24.68:9081";
        String endpoint = "127.0.0.1:8081";
        // 消息发送的目标Topic名称，需要提前创建。
        // sh bin/mqadmin updatetopic -n localhost:9876 -t TestTopic -c DefaultCluster
        String topic = "TestTopic";
        byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        // 2.创建通信配置
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoint)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();

        // 3.初始化Producer，需要设置通信配置以及预绑定的Topic。
//        Producer producer = ProducerSingleton.getInstance(topic);
        Producer producer = provider.newProducerBuilder()
                .setTopics(topic)
                .setClientConfiguration(configuration)
                .build();

        // 4.构建普通消息。
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                // 设置消息索引键，可根据关键字精确查找某条消息。
                .setKeys("messageKey")
                // 设置消息Tag，用于消费端根据指定Tag过滤消息。
                .setTag("messageTag")
                // 消息体。
                .setBody(body)
                .build();
        try {
            // 5.发送消息，需要关注发送结果，并捕获失败等异常。
            SendReceipt sendReceipt = producer.send(message);
            logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            logger.error("Failed to send message", e);
        }
        // producer.close();
    }
}


