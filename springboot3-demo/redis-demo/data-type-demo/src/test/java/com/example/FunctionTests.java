package com.example;

import com.example.config.RedisTemplateFunctionExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Function 脚本测试类
 * Created by hanqf on 2026/1/11 15:33.
 */

@SpringBootTest
public class FunctionTests {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RedisTemplateFunctionExecutor redisFunctionExecutor;

    @Test
    public void testRegister() {
        String lua = """
                #!lua name=ratelimit
                
                redis.register_function('allow', function(keys, args)
                
                    local rateKey = keys[1]
                    local maxReq  = tonumber(args[1])
                    local window  = tonumber(args[2])
                    local now     = tonumber(args[3])
                
                    local minTime = now - window
                
                    -- 1. 清理过期请求
                    redis.call("ZREMRANGEBYSCORE", rateKey, 0, minTime)
                
                    -- 2. 当前请求数
                    local count = tonumber(redis.call("ZCARD", rateKey))
                
                    if count >= maxReq then
                        return 'notOk'
                    end
                
                    -- 3. 记录请求
                    redis.call("ZADD", rateKey, now, now)
                
                    -- 4. 设置自动过期
                    redis.call("PEXPIRE", rateKey, window)
                
                    return 'isOk'
                end)
                """;

        String lib_name = "ratelimit";

        // 删除
        try {
            redisTemplate.execute((RedisCallback<Object>) connection ->
                    connection.execute("FUNCTION", "DELETE".getBytes(), lib_name.getBytes())
            );
        } catch (Exception e) {
            // 忽略
        } finally {
            // 注册
            redisTemplate.execute((RedisCallback<Object>) connection ->
                    connection.execute("FUNCTION", "LOAD".getBytes(), lua.getBytes())
            );
        }

    }

    @Test
    public void testExecute() {
        // 执行，报错，当前版本不支持对 FCALL 的返回值进行类型推断
        Object result = redisTemplate.execute((RedisCallback<Object>) connection ->
                connection.execute("FCALL",
                        "allow".getBytes(),
                        "1".getBytes(),
                        "rate:1001".getBytes(),
                        "5".getBytes(),
                        "60000".getBytes(),
                        "1705000000000".getBytes())
        );


        if (result instanceof byte[] bytes) {
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
        }else {
            throw new IllegalStateException("Unexpected FCALL return type: " + result.getClass());
        }
    }

    @Test
    public void testExecute2() {
        // 执行，报错，当前版本不支持对 FCALL 的返回值进行类型推断
        Object result = redisFunctionExecutor.fcall(
                "allow",
                List.of("rate:1001"),
                List.of("5", "60000", String.valueOf(System.currentTimeMillis()))
        );

        System.out.println("Raw result = " + result);
    }

}
