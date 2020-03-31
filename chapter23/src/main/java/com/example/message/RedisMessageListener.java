package com.example.message;/**
 * Created by hanqf on 2020/3/31 20:47.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/31 20:47
 */
@Component
public class RedisMessageListener implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
    @Override
    public void onMessage(Message message, byte[] bytes) {
        //消息体
        String body = new String(message.getBody());

        //渠道名称
        String topic = new String(bytes);
        logger.info(body);
        logger.info(topic);
    }
}
