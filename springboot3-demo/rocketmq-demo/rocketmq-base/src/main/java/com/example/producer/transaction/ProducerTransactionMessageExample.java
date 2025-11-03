package com.example.producer.transaction;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 生产者：事务消息
 * Created by hanqf on 2025/10/26 11:42.
 */

public class ProducerTransactionMessageExample {
    private static final Logger log = LoggerFactory.getLogger(ProducerTransactionMessageExample.class);

    private ProducerTransactionMessageExample() {
    }

    public static void main(String[] args) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        // 创建事务消息的Topic名称，需要提前创建。
        // sh bin/mqadmin updatetopic -n localhost:9876 -t TransactionTopic -c DefaultCluster -a +message.type=TRANSACTION
        String topic = "TransactionTopic";
        TransactionChecker checker = messageView -> {
            log.info("Receive transactional message check, message={}", messageView);
            // Return the transaction resolution according to your business logic.
            return TransactionResolution.COMMIT;
        };

        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081。
        String endpoint = "68.79.47.19:8081";
        ClientConfiguration configuration = ClientConfiguration.newBuilder().setEndpoints(endpoint).build();
//        final Producer producer = ProducerSingleton.getTransactionalInstance(checker, topic);
        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
        Producer producer = provider.newProducerBuilder()
                .setTopics(topic)
                .setClientConfiguration(configuration)
                .setTransactionChecker(checker) // 设置事务检查器
                .build();

        final Transaction transaction = producer.beginTransaction();
        // Define your message body.
        byte[] body = "This is a transaction message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        String tag = "yourMessageTagA";
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(topic)
                // Message secondary classifier of message besides topic.
                .setTag(tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-565ef26f5727")
                .setBody(body)
                .build();
        try {
            final SendReceipt sendReceipt = producer.send(message, transaction);
            log.info("Send transaction message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (Throwable t) {
            log.error("Failed to send message", t);
            return;
        }
        // Commit the transaction.
        transaction.commit();
        // Or rollback the transaction.
        // transaction.rollback();

        // Close the producer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // producer.close();
    }
}
