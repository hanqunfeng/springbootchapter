package com.example.serializer;

import org.apache.kafka.common.serialization.Serializer;

/**
 * @auth roykingw
 */
public class UserSerializer2 implements Serializer<User> {
    @Override
    public byte[] serialize(String topic, User data) {
        return KryoUtils.toBytes( data);
    }
}
