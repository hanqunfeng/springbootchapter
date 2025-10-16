package com.example.serializer;

/**
 *
 * Created by hanqf on 2025/10/16 11:11.
 */


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoUtils {
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false); // 不要求类提前注册
        kryo.setReferences(true);            // 支持循环引用
        return kryo;
    });

    /**
     * 将对象序列化为 byte[]
     */
    public static byte[] toBytes(Object obj) {
        if (obj == null) return new byte[0];

        Kryo kryo = kryoThreadLocal.get();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Kryo serialize error", e);
        }
    }

    /**
     * 从 byte[] 反序列化为对象（泛型安全）
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromBytes(byte[] bytes, Class<T> type) {
        if (bytes == null || bytes.length == 0) return null;

        Kryo kryo = kryoThreadLocal.get();
        try (Input input = new Input(new ByteArrayInputStream(bytes))) {
            Object obj = kryo.readClassAndObject(input);
            return (T) obj;
        } catch (Exception e) {
            throw new RuntimeException("Kryo deserialize error: " + type, e);
        }
    }

    public static Object fromBytes(byte[] bytes) {
        Kryo kryo = kryoThreadLocal.get();
        Input input = new Input(new ByteArrayInputStream(bytes));
        Object obj = kryo.readClassAndObject(input);
        input.close();
        return obj;
    }
}

