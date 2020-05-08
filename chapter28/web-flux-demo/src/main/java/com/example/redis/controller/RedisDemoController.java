package com.example.redis.controller;

/**
 * <p></p>
 * Created by hanqf on 2020/5/8 14:41.
 */

import com.alibaba.fastjson.JSON;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/redisusers")
public class RedisDemoController {
    /**
     * redis响应式模板
    */
    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;


    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Reactive");
    }

    @PostMapping("/save")
    public Mono<Boolean> saveUser(@RequestBody User user) {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        return reactiveHashOperations.put("USER_HS", String.valueOf(user.getId()), JSON.toJSONString(user));
    }

    @GetMapping("/info/{id}")
    public Mono<User> info(@PathVariable Long id) {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        Mono<String> hval = reactiveHashOperations.get("USER_HS", String.valueOf(id));
        return hval.map(e -> JSON.parseObject(e, User.class));
    }

    @GetMapping("/all")
    public Flux<User> findAll() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        //先获取hash中的全部key
        Flux<String> hkeys = reactiveHashOperations.keys("USER_HS");

        return hkeys.flatMap(id -> {
            //通过每一个key获取对应的值，即json字符串，并转换为User对象
            Mono<String> userHs = reactiveHashOperations.get("USER_HS", String.valueOf(id));
            return userHs.map(e -> JSON.parseObject(e, User.class));
        });
    }

    @GetMapping("/all2")
    public Flux<User> findAll2() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        //先获取hash中的全部key
        Flux<String> hvalues = reactiveHashOperations.values("USER_HS");
        return hvalues.map(value -> JSON.parseObject(value, User.class));
    }

    @GetMapping("/all3")
    public Flux<User> findAll3() {
        ReactiveHashOperations<String, String, String> reactiveHashOperations = reactiveStringRedisTemplate.opsForHash();
        Flux<Map.Entry<String,String>> hentries = reactiveHashOperations.entries("USER_HS");
        return hentries.map(map -> JSON.parseObject(map.getValue(), User.class));
    }




}
