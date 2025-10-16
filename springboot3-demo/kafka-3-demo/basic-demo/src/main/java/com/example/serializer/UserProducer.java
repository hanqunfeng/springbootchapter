package com.example.serializer;

import com.example.basic.KafkaManager;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @auth roykingw
 */
public class UserProducer {
    private static final String TOPIC = "userTopic";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //PART1:设置发送者相关属性
        Properties props = KafkaManager.getBootstrapServersConfig();
        // 配置key的序列化类
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        // 配置value的序列化类
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"com.example.serializer.UserSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"com.example.serializer.UserSerializer2");

        Producer<String,User> producer = new KafkaProducer<>(props);
        for(int i = 0; i < 5; i++) {
            User user = new User((long)i, "user" + i, i % 2);
            //Part2:构建消息
            ProducerRecord<String, User> record = new ProducerRecord<>(TOPIC, Integer.toString(i), user);
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
