package com.example.quickstart;

import com.example.AclUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import java.nio.charset.StandardCharsets;

/**
 * https://github.com/apache/rocketmq/blob/develop/example/src/main/java/org/apache/rocketmq/example/quickstart/Consumer.java
 * This example shows how to subscribe and consume messages using providing {@link DefaultMQPushConsumer}.
 */
public class Consumer {

    public static final String CONSUMER_GROUP = "please_rename_unique_group_name_4";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
//    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:8080";
//    public static final String TOPIC = "TopicTest";
    public static final String TOPIC = "TestTopic";

    public static void main(String[] args) throws MQClientException {

        /*
         * Instantiate with specified consumer group name.
         */
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP, AclUtil.getAclRPCHook());

        /*
         * Specify name server addresses.
         * <p/>
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         * consumer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */
        // Uncomment the following line while debugging, namesrvAddr should be set to your local address
         consumer.setNamesrvAddr(DEFAULT_NAMESRVADDR);

        /*
         * Specify where to start in case the specific consumer group is a brand-new one.
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        /*
         * Subscribe one more topic to consume.
         */
        consumer.subscribe(TOPIC, "*");

        /*
         *  Register callback to execute on arrival of messages fetched from brokers.
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (msg, context) -> {
            System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msg);
            msg.forEach(messageExt -> System.out.println("收到消息:"+new String(messageExt.getBody(), StandardCharsets.UTF_8)));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        /*
         *  Launch the consumer instance.
         */
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
