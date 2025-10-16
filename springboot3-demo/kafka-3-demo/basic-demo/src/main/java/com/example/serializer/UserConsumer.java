package com.example.serializer;

import com.example.basic.KafkaManager;
import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @auth roykingw
 */
public class UserConsumer {
    private static final String TOPIC = "userTopic";

    public static void main(String[] args) {
        //PART1:设置发送者相关属性
        Properties props = KafkaManager.getBootstrapServersConfig();
        //每个消费者要指定一个group
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "testUser");
        //key序列化类
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //value序列化类
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.example.serializer.UserDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.example.serializer.UserDeserializer2");
        Consumer<String, User> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
//        consumer.seekToBeginning();
        while (true) {
            //PART2:拉取消息
            // 100毫秒超时时间
            ConsumerRecords<String, User> records = consumer.poll(Duration.ofNanos(100));
            //PART3:处理消息
            for (ConsumerRecord<String, User> record : records) {
                System.out.println("offset = " + record.offset() + "; key = " + record.key() + "; value= " + record.value());
            }
            //提交offset，消息就不会重复推送。
            consumer.commitSync(); //同步提交，表示必须等到offset提交完毕，再去消费下一批数据。
//            consumer.commitAsync(); //异步提交，表示发送完提交offset请求后，就开始消费下一批数据了。不用等到Broker的确认。
        }
    }
}
