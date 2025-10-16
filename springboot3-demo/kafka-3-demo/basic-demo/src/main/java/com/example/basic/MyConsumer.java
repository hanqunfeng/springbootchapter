package com.example.basic;

import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 *
 * Created by hanqf on 2025/10/15 17:35.
 */


public class MyConsumer {

    public static void main(String[] args) {
        //PART1:设置发送者相关属性
        Properties props = KafkaManager.getBootstrapServersConfig();

        //每个消费者要指定一个group
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        //key序列化类
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //value序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        // 忽略内部主题，默认为 true
//        props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG,true);
//        //第一次启动，没有offset偏移量，就从头开始消费。
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // 创建一个消费者
        // 指定一个group
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(KafkaManager.topicName));
        //自行调整Offset
//        consumer.seekToBeginning(Arrays.asList(new TopicPartition(TOPIC,0)));
        while (true) {
            //PART2:拉取消息
            // 100毫秒超时时间
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofNanos(100));
//            records.partitions().forEach(topicPartition -> {
//                String key = topicPartition.topic()+topicPartition.partition();
//                List<ConsumerRecord<String, String>> partionRecords = records.records(topicPartition);
//                long value = partionRecords.get(partionRecords.size()-1).offset();
//
//            });
            //PART3:处理消息
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("partition = "+record.partition()+"; offset = " + record.offset() + "; key = " + record.key() + "; value= " + record.value());
            }


            //提交offset，消息就不会重复推送。
            consumer.commitSync(); //同步提交，表示必须等到offset提交完毕，再去消费下一批数据。
//            consumer.commitAsync(); //异步提交，表示发送完提交offset请求后，就开始消费下一批数据了。不用等到Broker的确认。
        }
    }
}

