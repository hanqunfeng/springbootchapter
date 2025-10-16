package com.example.basic;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @auth roykingw
 * 演示生产者消息幂等性问题
 */
public class MyProducer2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //PART1:设置发送者相关属性
        Properties props = KafkaManager.getBootstrapServersConfig();
        // 配置key的序列化类
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        // 配置value的序列化类
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        Producer<String,String> producer1 = new KafkaProducer<>(props);
        Producer<String,String> producer2 = new KafkaProducer<>(props);
        for(int i = 0; i < 5; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(KafkaManager.topicName, Integer.toString(i), "MyProducer" + i);
            //同步发送：获取服务端应答消息前，会阻塞当前线程。
            RecordMetadata recordMetadata = producer1.send(record).get();
            String topic = recordMetadata.topic();
            int partition = recordMetadata.partition();
            long offset = recordMetadata.offset();
            String message = recordMetadata.toString();
            System.out.println("message:["+ message+"] sended with topic:"+topic+"; partition:"+partition+ ";offset:"+offset);

            RecordMetadata recordMetadata2 = producer2.send(record).get();
            String topic2 = recordMetadata2.topic();
            int partition2 = recordMetadata2.partition();
            long offset2 = recordMetadata2.offset();
            String message2 = recordMetadata2.toString();
            System.out.println("message2:["+ message2+"] sended with topic:"+topic2+"; partition:"+partition2+ ";offset:"+offset2);
        }
        //消息处理完才停止发送者。
        producer1.close();
        producer2.close();
    }
}
