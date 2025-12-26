package com.example.demo.stream;

import com.example.demo.CommonUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/12/20 12:56.
 */

@Component
public class StreamUtil extends CommonUtil {
//    @Autowired
//    protected StringRedisTemplate redisTemplate;

    public void demo(){
        redisTemplate.opsForStream().add("stream1", Map.of("key1", "value1", "key2", "value2"));
    }

}
