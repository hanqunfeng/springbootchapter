package com.example;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/10/16 09:57.
 */


public class MyInterceptor implements ProducerInterceptor {
    //发送消息时触发
    @Override
    public ProducerRecord onSend(ProducerRecord producerRecord) {
        System.out.println("prudocerRecord : " + producerRecord.toString());
        return producerRecord;
    }

    //收到服务端响应时触发
    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        System.out.println("acknowledgement recordMetadata:"+recordMetadata.toString());
    }

    //连接关闭时触发
    @Override
    public void close() {
        System.out.println("producer closed");
    }

    //整理配置项，在发送消息前触发
    @Override
    public void configure(Map<String, ?> map) {
        System.out.println("=====config start======");
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            System.out.println("entry.key:"+entry.getKey()+" === entry.value: "+entry.getValue());
        }
        System.out.println("=====config end======");
    }
}

