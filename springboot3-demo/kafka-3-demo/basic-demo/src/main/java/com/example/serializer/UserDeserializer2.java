package com.example.serializer;

import org.apache.kafka.common.serialization.Deserializer;

/**
 * @auth roykingw
 */
public class UserDeserializer2 implements Deserializer<User> {
    @Override
    public User deserialize(String topic, byte[] data) {
        return KryoUtils.fromBytes(data, User.class);
    }
}
