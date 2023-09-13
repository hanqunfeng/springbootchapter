package com.example.redis;

/**
 * <h1>RedisDemoController</h1>
 * Created by hanqf on 2023/8/30 16:58.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisDemoController {
    /**
     * redis响应式模板
     */
    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveStringRedisTemplate;

    /**
     * 自定义redis响应式工具类
     */
    @Autowired
    private ReactiveRedisUtil reactiveRedisUtil;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Reactive");
    }

    @PostMapping("/save")
    public Mono<Boolean> saveUser(@RequestBody UserEntity userEntity) throws JsonProcessingException {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        return reactiveHashOperations.put("USER_HS", String.valueOf(userEntity.getId()), objectMapper.writeValueAsString(userEntity));
    }

    @GetMapping("/info/{id}")
    public Mono<UserEntity> info(@PathVariable Long id) {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        Mono<String> hval = reactiveHashOperations.get("USER_HS", String.valueOf(id));
        return hval.map(e -> {
            try {
                return objectMapper.readValue(e, UserEntity.class);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @GetMapping("/all")
    public Flux<UserEntity> findAll() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        //先获取hash中的全部key
        Flux<String> hkeys = reactiveHashOperations.keys("USER_HS");

        return hkeys.flatMap(id -> {
            //通过每一个key获取对应的值，即json字符串，并转换为User对象
            Mono<String> userHs = reactiveHashOperations.get("USER_HS", String.valueOf(id));
            return userHs.map(e -> {
                try {
                    return objectMapper.readValue(e, UserEntity.class);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
    }

    @GetMapping("/all2")
    public Flux<UserEntity> findAll2() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        //先获取hash中的全部value
        Flux<String> hvalues = reactiveHashOperations.values("USER_HS");
        return hvalues.map(value -> {
            try {
                return objectMapper.readValue(value, UserEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping("/all3")
    public Flux<UserEntity> findAll3() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        Flux<Map.Entry<String, String>> hentries = reactiveHashOperations.entries("USER_HS");
        return hentries.map(map -> {
            try {
                return objectMapper.readValue(map.getValue(), UserEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //curl http://localhost:8080/redisusers/stream
    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<UserEntity> findAllStream() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        Flux<Map.Entry<String, String>> hentries = reactiveHashOperations.entries("USER_HS");
        return hentries.map(map -> {
                    try {
                        return objectMapper.readValue(map.getValue(), UserEntity.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .delayElements(Duration.ofSeconds(1)); //每秒返回一条数据，模拟流式响应
    }


    @PostMapping("/save2")
    public Mono<Boolean> saveUser2(@RequestBody UserEntity userEntity) {
        return reactiveRedisUtil.putHashValue("USER_HS2", String.valueOf(userEntity.getId()), userEntity);
    }


    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserEntity> getUser(@PathVariable Long id) {
        return reactiveRedisUtil.getHashValue("USER_HS2", String.valueOf(id));
    }

    @GetMapping(value = "/lock")
    public Mono<Boolean> lock() {
        return reactiveRedisUtil.tryGetDistributedLock("testKey", "testRequest", 1000);
    }

    @GetMapping(value = "/res-lock")
    public Mono<Boolean> resLock() {
        return reactiveRedisUtil.releaseDistributedLock("testKey", "testRequest");
    }


    @GetMapping(value = "/lua")
    public Flux<Object> luaScript() {
        String luaScript = "return 'Hello World 123'";

        //实际上不需要这么做，executeLuaScript方法执行时就会将lua脚本预加载到redis server中
        return reactiveRedisUtil.luaScriptExists(luaScript).doOnNext(exist -> {
            if (!exist) {
                log.info(reactiveRedisUtil.luaScriptLoad(luaScript));
            } else {
                log.info("luaScript exist");
            }
        }).thenMany(reactiveRedisUtil.executeLuaScript(luaScript, new ArrayList<>(), new ArrayList<>()));
    }

    @GetMapping(value = "/lua2")
    public Flux<Object> luaScript2() {
        String luaScript = "return 'Hello World 456'";
       return reactiveRedisUtil.executeLuaScript(luaScript, new ArrayList<>(), new ArrayList<>());
    }
}
