package com.example.producer.delay;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 生产者：定时/延时消息
 * Created by hanqf on 2025/10/26 11:19.
 */

public class ProducerDelayMessageExample {
    private static final Logger log = LoggerFactory.getLogger(ProducerDelayMessageExample.class);

    private ProducerDelayMessageExample() {
    }

    public static void main(String[] args) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        // 延时消息的Topic名称，需要提前创建。
        // sh bin/mqadmin updatetopic -n localhost:9876 -t DelayTopic -c DefaultCluster -a +message.type=DELAY
        String topic = "DelayTopic";
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
        byte[] body = "This is a delay message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        String tag = "yourMessageTagA";
        Duration messageDelayTime = Duration.ofSeconds(10);
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(topic)
                // Message secondary classifier of message besides topic.
                .setTag(tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-3ee439f945d7")
                // 设置消息的预期传递时间戳。本示例设置为10秒后传递。
                .setDeliveryTimestamp(System.currentTimeMillis() + messageDelayTime.toMillis())
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
