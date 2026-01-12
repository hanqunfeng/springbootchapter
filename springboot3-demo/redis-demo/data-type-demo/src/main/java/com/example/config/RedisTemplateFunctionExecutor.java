package com.example.config;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * RedisTemplateFunctionExecutor
 * Created by hanqf on 2026/1/11 16:21.
 */

@Component
public class RedisTemplateFunctionExecutor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisSerializer<String> stringSerializer;

    public RedisTemplateFunctionExecutor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringSerializer = RedisSerializer.string();
    }

    public Object fcall(String functionName, List<String> keys, List<String> args) {

        Object raw = redisTemplate.execute((RedisConnection connection) -> {

            List<byte[]> command = new ArrayList<>();

            // FCALL functionName numKeys ...
            command.add(stringSerializer.serialize(functionName));
            command.add(stringSerializer.serialize(String.valueOf(keys.size())));

            // keys
            for (String key : keys) {
                command.add(stringSerializer.serialize(key));
            }

            // args
            for (String arg : args) {
                command.add(stringSerializer.serialize(arg));
            }

            byte[][] commandArray = command.toArray(new byte[0][]);

            return connection.execute("FCALL", commandArray);
        });

        if (raw instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        throw new IllegalStateException("Unexpected FCALL return type: " + raw.getClass());
    }
}
