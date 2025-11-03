package com.example.producer.normal;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发送消息示例:异步发送
 * Created by hanqf on 2025/10/25 22:40.
 */



public class AsyncProducerExample {
    private static final Logger log = LoggerFactory.getLogger(AsyncProducerExample.class);

    private AsyncProducerExample() {
    }

    public static void main(String[] args) throws ClientException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081。
        String endpoint = "68.79.47.19:8081";
        // 创建消息的Topic名称，需要提前创建。
        // sh bin/mqadmin updatetopic -n localhost:9876 -t TestTopic -c DefaultCluster
        String topic = "TestTopic";

        ClientConfiguration configuration = ClientConfiguration.newBuilder().setEndpoints(endpoint).build();
//        final Producer producer = ProducerSingleton.getInstance(topic);
        Producer producer = provider.newProducerBuilder()
                .setTopics(topic)
                .setClientConfiguration(configuration)
                .build();

        // Define your message body.
        byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        String tag = "yourMessageTagA";
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(topic)
                // Message secondary classifier of message besides topic.
                .setTag(tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-0e094a5f9d85")
                .setBody(body)
                .build();
        // Set individual thread pool for send callback.
        final CompletableFuture<SendReceipt> future = producer.sendAsync(message);
        ExecutorService sendCallbackExecutor = Executors.newCachedThreadPool();
        future.whenCompleteAsync((sendReceipt, throwable) -> {
            if (null != throwable) {
                log.error("Failed to send message", throwable);
                // Return early.
                return;
            }
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        }, sendCallbackExecutor);
        // Block to avoid exist of background threads.
        Thread.sleep(Long.MAX_VALUE);
        // Close the producer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // producer.close();
    }
}
