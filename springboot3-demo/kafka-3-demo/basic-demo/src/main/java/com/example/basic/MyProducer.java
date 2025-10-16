package com.example.basic;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 *
 * Created by hanqf on 2025/10/15 17:40.
 */


public class MyProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //PART1:设置发送者相关属性
        Properties props = KafkaManager.getBootstrapServersConfig();

        // 添加拦截器
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,"com.example.basic.MyInterceptor");
        // 配置key的序列化类
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        // 配置value的序列化类
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

//        props.put(ProducerConfig.ACKS_CONFIG,"all");

        Producer<String,String> producer = new KafkaProducer<>(props);
//        CountDownLatch latch = new CountDownLatch(5);
        for(int i = 0; i < 2; i++) {
            //Part2:构建消息,topic 不存在时，会根据服务端的默认配置自动创建
            ProducerRecord<String, String> record = new ProducerRecord<>(KafkaManager.topicName, Integer.toString(i), "MyProducer" + i);
            //Part3:发送消息
            //单向发送：不关心服务端的应答。
//            producer.send(record);
//            System.out.println("message "+i+" sended");
            //同步发送：获取服务端应答消息前，会阻塞当前线程。
            RecordMetadata recordMetadata = producer.send(record).get();
            String topic = recordMetadata.topic();
            int partition = recordMetadata.partition();
            long offset = recordMetadata.offset();
            String message = recordMetadata.toString();
            System.out.println("message:["+ message+"] sended with topic:"+topic+"; partition:"+partition+ ";offset:"+offset);
            //异步发送：消息发送后不阻塞，服务端有应答后会触发回调函数
//            producer.send(record, new Callback() {
//                @Override
//                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//                    if(null != e){
//                        System.out.println("消息发送失败,"+e.getMessage());
//                        e.printStackTrace();
//                    }else{
//                        String topic = recordMetadata.topic();
//                        long offset = recordMetadata.offset();
//                        String message = recordMetadata.toString();
//                        System.out.println("message:["+ message+"] sended with topic:"+topic+";offset:"+offset);
//                    }
//                    latch.countDown();
//                }
//            });
        }
        //消息处理完才停止发送者。
//        latch.await();
        producer.close();
    }
}

