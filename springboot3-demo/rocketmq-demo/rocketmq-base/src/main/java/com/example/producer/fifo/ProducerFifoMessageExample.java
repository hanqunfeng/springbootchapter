package com.example.producer.fifo;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 生产者：顺序消息
 *
 * 顺序消息，保证同一个组内的消息顺序。
 * 创建消息时指定 消息组(MessageGroup)，消息组决定消息传递顺序。
 * Created by hanqf on 2025/10/26 11:02.
 */

public class ProducerFifoMessageExample {
    private static final Logger log = LoggerFactory.getLogger(ProducerFifoMessageExample.class);

    private ProducerFifoMessageExample() {
    }

    public static void main(String[] args) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        // 顺序消息的Topic名称，需要提前创建。
        // sh bin/mqadmin updatetopic -n localhost:9876 -t FifoTopic -c DefaultCluster -a +message.type=FIFO
        String topic = "FifoTopic";
        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081。
        String endpoint = "68.79.47.19:8081";
        ClientConfiguration configuration = ClientConfiguration.newBuilder().setEndpoints(endpoint).build();

        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
//        Producer producer = ProducerSingleton.getInstance(topic);
        Producer producer = provider.newProducerBuilder()
                .setTopics(topic)
                .setClientConfiguration(configuration)
                .build();

        // Define your message body.
        byte[] body = "This is a FIFO message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        String tag = "yourMessageTagA";
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(topic)
                // Message secondary classifier of message besides topic.
                .setTag(tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-1ff69ada8e0e")
                // 消息组决定消息传递顺序。与普通消息唯一的区别就是需要设置消息组
                .setMessageGroup("yourMessageGroup0")
                .setBody(body)
                .build();
        try {
            final SendReceipt sendReceipt = producer.send(message);
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (Throwable t) {
            log.error("Failed to send message", t);
        }
        // Close the producer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // producer.close();
    }
}
