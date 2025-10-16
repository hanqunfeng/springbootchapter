package com.example.basic;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Author： roy
 * Description： 从指定offset开始消费
 **/
public class MyConsumer2 {
//    private static final String TOPIC = "disTopic";
    private static final String TOPIC = "__consumer_offsets";
    public static void main(String[] args) {
        //PART1:设置发送者相关属性
        Properties props = KafkaManager.getBootstrapServersConfig();
        //kafka地址
        //每个消费者要指定一个group
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test01");
        //key序列化类
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //value序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG,true);
        //第一次启动，没有offset偏移量，就从头开始消费。
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        //手动指定消费分区
        consumer.assign(Arrays.asList(new TopicPartition(TOPIC,8)));
        //订阅Topic，自动分配消费分区。不能和手动方式混合使用。
//        consumer.subscribe(Arrays.asList(TOPIC));
        //自行调整Offset
        consumer.seekToBeginning(Arrays.asList(new TopicPartition(TOPIC,8)));
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
