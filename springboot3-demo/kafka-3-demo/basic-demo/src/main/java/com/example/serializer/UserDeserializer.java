package com.example.serializer;

import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @auth roykingw
 */
public class UserDeserializer implements Deserializer<User> {
    @Override
    public User deserialize(String topic, byte[] data) {
        //简单粗暴的序列化
//        return JSON.parseObject(data, User.class);
        //自定义序列化
        ByteBuffer dataBuffer = ByteBuffer.allocate(data.length);
        dataBuffer.put(data);
        dataBuffer.flip();
        long id = dataBuffer.getLong();
        int nameLength = dataBuffer.getInt();
        String name = new String(dataBuffer.get(data, 12, nameLength).array(), StandardCharsets.UTF_8).trim();
        int sex = dataBuffer.getInt();
        return new User(id,name,sex);
    }
}
