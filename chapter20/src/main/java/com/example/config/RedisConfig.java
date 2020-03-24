package com.example.config;/**
 * Created by hanqf on 2020/3/23 15:38.
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;

/**
 * @author hanqf
 * @date 2020/3/23 15:38
 */
@Configuration
public class RedisConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        //redisTemplate.setValueSerializer(stringSerializer); //value可以不设置，这里只是为了在终端查看方便
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer); //value可以不设置，这里只是为了在终端查看方便
    }
}
