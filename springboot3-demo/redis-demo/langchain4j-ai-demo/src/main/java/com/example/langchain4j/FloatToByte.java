package com.example.langchain4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FloatToByte {
    public static void main(String[] args) {
        float[] vector = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
        byte[] bytes = toFloat32Bytes(vector);
        System.out.println(toRedisHex(bytes));
    }

    public static byte[] toFloat32Bytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer
                .allocate(vector.length * 4)
                .order(ByteOrder.LITTLE_ENDIAN); // Redis 要求小端序

        for (float v : vector) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }

    public static String toRedisHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 4);
        for (byte b : bytes) {
            sb.append("\\x");
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
}
