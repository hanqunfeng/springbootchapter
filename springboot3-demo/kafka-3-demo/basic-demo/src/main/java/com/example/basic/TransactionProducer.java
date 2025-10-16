package com.example.basic;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @auth roykingw
 */
public class TransactionProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = KafkaManager.getBootstrapServersConfig();
        // 事务ID。
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"111");
        // 配置key的序列化类
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        // 配置value的序列化类
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        Producer<String,String> producer = new KafkaProducer<>(props);
        producer.initTransactions();
        producer.beginTransaction();
        try{
            for(int i = 0; i < 5; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>(KafkaManager.topicName, Integer.toString(i), "MyProducer" + i);
                //异步发送。
                producer.send(record);
            }
            producer.commitTransaction();
        }catch (ProducerFencedException e){
            producer.abortTransaction();
        }finally {
            producer.close();
        }
    }
}
