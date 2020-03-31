package com.example.config;/**
 * Created by hanqf on 2020/3/23 15:38.
 */


import com.example.message.RedisMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;

/**
 * @author hanqf
 * @date 2020/3/23 15:38
 */
@Configuration
public class RedisConfig {

    @Autowired
    private RedisTemplate redisTemplate;
    //Redis 连接工厂
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    //Redis 消息监昕器
    @Autowired
    private RedisMessageListener redisMessageListener;
    //任务池
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @PostConstruct
    public void init() {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer); //value可以不设置，这里只是为了在终端查看方便
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer); //value可以不设置，这里只是为了在终端查看方便
    }

    /**
     * ＊创建任务池，运行线程等待处理 Redis 的消息
     */
    @Bean
    public ThreadPoolTaskScheduler initTaskScheduler() {
        if (threadPoolTaskScheduler != null) {
            return threadPoolTaskScheduler;
        }
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(20);
        return threadPoolTaskScheduler;
    }

    /**
     * ＊定义 Redis 的监听容器
     * return 监昕容器
     */
    @Bean
    public RedisMessageListenerContainer initRedisContainer() {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        //Redis 连接工厂
        container.setConnectionFactory(redisConnectionFactory);
        //消息体的序列化器，可以不设置这个序列化器，默认使用redisTemplate的
        container.setTopicSerializer(redisTemplate.getValueSerializer());
        //设置运行任务池
        container.setTaskExecutor(initTaskScheduler());
        //定义监听渠道，名称为 topic1
        Topic topic = new ChannelTopic("topic1");
        //使用监听器监听 Redis 的消息，可以设置多个消息监听
        container.addMessageListener(redisMessageListener, topic);
        return container;
    }
}
