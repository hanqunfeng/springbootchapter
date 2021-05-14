package com.example.consumer;

import com.example.message.QueenMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * <h1>发布订阅消息消费者</h1>
 * Created by hanqf on 2021/5/14 15:27.
 */

@Component
@Slf4j
public class TopicConsumerListenter {

    @JmsListener(destination = "message.topic", containerFactory = "topicListenerFactory")
    public void receiver1(QueenMessage queenMessage) {
        log.info("TopicConsumer : receiver1 : " + queenMessage);
    }

    @JmsListener(destination = "message.topic", containerFactory = "topicListenerFactory")
    public void receiver2(QueenMessage queenMessage) {
        log.info("TopicConsumer : receiver2 : " + queenMessage);
    }

    @JmsListener(destination = "message.topic", containerFactory = "topicListenerFactory")
    // 回调另外一个消息，
    // 这里注意，这两个消息必须在同一个containerFactory，
    // 而且返回值必须是被回调消费者的参数
    @SendTo("message.queue2")
    public QueenMessage receiver3(QueenMessage queenMessage) {
        log.info("TopicConsumer : receiver3 : " + queenMessage);
        return queenMessage;
    }

    @JmsListener(destination = "message.queue2", containerFactory = "topicListenerFactory")
    public void callback(QueenMessage queenMessage) {
        log.info("消费者接收数据2 : " + queenMessage);
    }
}
