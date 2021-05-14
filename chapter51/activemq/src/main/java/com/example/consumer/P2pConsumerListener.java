package com.example.consumer;

import com.example.message.QueenMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * <h1>点对点消费者示例</h1>
 * Created by hanqf on 2021/5/14 14:56.
 */


@Component
@Slf4j
public class P2pConsumerListener {
    @JmsListener(destination = "message.queue",containerFactory = "queueListenerFactory")
    public void insertVisitLog(QueenMessage queenMessage) {
        log.info("消费者接收数据 : " + queenMessage);
    }
}
