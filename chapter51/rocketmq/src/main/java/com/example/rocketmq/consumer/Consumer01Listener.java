package com.example.rocketmq.consumer;

import com.example.rocketmq.common.RocketMqMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * <h1>消息订阅者</h1>
 *
 * @author hanqf
 * @date 2021/6/24 15:51
 */

@Slf4j
@Component
// topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "${rocketmq.consume.topic}", consumerGroup = "${rocketmq.consume.group}")
public class Consumer01Listener implements RocketMQListener<RocketMqMessage> {

    @Override
    public void onMessage(RocketMqMessage rocketMqMessage) {
        log.info("======Consumer01Listener,我收到了消息,消息内容为:{}",rocketMqMessage);
    }
}
