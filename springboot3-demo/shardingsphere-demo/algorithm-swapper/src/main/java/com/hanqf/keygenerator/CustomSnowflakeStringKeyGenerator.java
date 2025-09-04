package com.hanqf.keygenerator;

import org.apache.shardingsphere.infra.algorithm.core.context.AlgorithmSQLContext;
import org.apache.shardingsphere.infra.algorithm.keygen.core.KeyGenerateAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义雪花算法主键生成器（返回字符串类型）
 */
public final class CustomSnowflakeStringKeyGenerator implements KeyGenerateAlgorithm {

    // 数据中心ID，最大值31
    private long datacenterId = 1L;
    // 工作节点ID，最大值31
    private long workerId = 1L;
    // 序列号
    private AtomicLong sequence = new AtomicLong(0L);

    // 开始时间戳 (2020-01-01)
    private final long twepoch = 1577836800000L;

    // 位数分配
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);       // 31
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); // 31

    private final long workerIdShift = sequenceBits;                    // 12
    private final long datacenterIdShift = sequenceBits + workerIdBits; // 17
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits; // 22

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);      // 4095

    private long lastTimestamp = -1L;

    @Override
    public void init(Properties props) {
        if (props.containsKey("datacenterId")) {
            datacenterId = Long.parseLong(props.getProperty("datacenterId"));
        }
        if (props.containsKey("workerId")) {
            workerId = Long.parseLong(props.getProperty("workerId"));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId out of range");
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("workerId out of range");
        }
    }

    @Override
    public Collection<? extends Comparable<?>> generateKeys(AlgorithmSQLContext context, int count) {
        List<String> keys = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            keys.add(String.valueOf(nextId()));
        }
        return keys;
    }

    private synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (lastTimestamp == timestamp) {
            long seq = (sequence.incrementAndGet()) & sequenceMask;
            if (seq == 0) {
                // 序列号溢出，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence.set(0L);
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence.get();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    @Override
    public String getType() {
        return "CUSTOM_SNOWFLAKE_STRING";
    }
}
