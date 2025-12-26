package com.example.demo.bitmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by hanqf on 2025/12/18 16:42.
 */

@Component
public class BitmapUtil {
    @Autowired
    protected StringRedisTemplate redisTemplate;

    public void demo(){
        //        BITFIELD limits
        //            OVERFLOW SAT
        //            SET u4 0 3
        //            INCRBY u4 0 20
        //            GET u4 0
        BitFieldSubCommands.BitFieldSet bitFieldSet = BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.unsigned(4), BitFieldSubCommands.Offset.offset(0), 3);
        BitFieldSubCommands.BitFieldIncrBy bitFieldIncrBy = BitFieldSubCommands.BitFieldIncrBy.create(BitFieldSubCommands.BitFieldType.unsigned(4), BitFieldSubCommands.Offset.offset(0), 20, BitFieldSubCommands.BitFieldIncrBy.Overflow.SAT);
        BitFieldSubCommands.BitFieldGet bitFieldGet = BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.unsigned(4), BitFieldSubCommands.Offset.offset(0));
        BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create(bitFieldSet, bitFieldIncrBy, bitFieldGet);
        List<Long> limits = redisTemplate.opsForValue().bitField("limits", bitFieldSubCommands);

    }

    /**
     * BITCOUNT key [start end]
     * <p>
     * 统计 bit=1 的数量
     */
    public Long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.stringCommands().bitCount(key.getBytes(StandardCharsets.UTF_8))
        );
    }

    public Long bitCount(String key, long start, long end) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.stringCommands().bitCount(key.getBytes(), start, end)
        );
    }

    /**
     * BITPOS key bit [start] [end]
     * <p>
     * bit = false → 查找第一个 0
     * bit = true → 查找第一个 1
     * <p>
     * 返回值是 bit 索引（不是 byte）
     */
    public Long bitPos(String key, boolean bit) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.stringCommands().bitPos(key.getBytes(), bit)
        );
    }

    public Long bitPos(String key, boolean bit, long start, long end) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.stringCommands().bitPos(key.getBytes(), bit, Range.open(start, end))
        );
    }

    /**
     * BITOP operation destKey key [key ...]
     * <p>
     * operation: AND\OR\XOR\NOT
     * <p>
     * 运算结果保存在 destKey 中
     */
    public Long bitOp(String destKey, RedisStringCommands.BitOperation operation, String... sourceKeys) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            byte[][] keys = Arrays.stream(sourceKeys)
                    .map(String::getBytes)
                    .toArray(byte[][]::new);

            return connection.stringCommands().bitOp(
                    operation,
                    destKey.getBytes(),
                    keys
            );
        });
    }


}
