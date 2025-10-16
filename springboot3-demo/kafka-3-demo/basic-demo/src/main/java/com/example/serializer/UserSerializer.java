package com.example.serializer;

import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @auth roykingw
 */
public class UserSerializer implements Serializer<User> {
    @Override
    public byte[] serialize(String topic, User data) {
        //简单粗暴的自定义序列化
//        return JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
        //效率更高的自定义序列化
        byte[] nameBytes = data.getName().getBytes(StandardCharsets.UTF_8);
        //id: long 8byte + 4: int name的长度 +  name:string 不定长 + sex: int 4byte
        int cap = 8 + 4 + nameBytes.length+4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(cap);
        byteBuffer.putLong(data.getId());
        byteBuffer.putInt(nameBytes.length);
        byteBuffer.put(nameBytes);
        byteBuffer.putInt(data.getSex());
        return byteBuffer.array();
    }
}
