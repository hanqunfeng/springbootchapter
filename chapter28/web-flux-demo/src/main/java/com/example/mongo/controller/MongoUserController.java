package com.example.mongo.controller;

import com.example.mongo.model.MongoUser;
import com.example.mongo.service.MongoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * <p></p>
 * Created by hanqf on 2020/5/8 13:30.
 */

@RestController
@RequestMapping("/mongouser")
public class MongoUserController {

    @Autowired
    private MongoUserService userService;

    @PostMapping
    public Mono<MongoUser> save(@RequestBody MongoUser user) {
        return this.userService.save(user);
    }

    @DeleteMapping("/{username}")
    public Mono<Long> deleteByUsername(@PathVariable String username) {
        return this.userService.deleteByUserName(username);
    }

    @GetMapping("/{username}")
    public Mono<MongoUser> findByUsername(@PathVariable String username) {
        return this.userService.findByUserName(username);
    }

    //这样会一次性返回全部数据，而不是响应式的返回数据
    @GetMapping
    public Flux<MongoUser> findAll() {
        return this.userService.findAll();
    }

    //这样是响应式的返回数据
    //因为返回的是json，所以用MediaType.APPLICATION_STREAM_JSON_VALUE，新版本推荐使用MediaType.APPLICATION_NDJSON_VALUE
    //使用 curl http://localhost:8080/mongouser/stream 可以看到效果，每秒打印一条记录
    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<MongoUser> findAllStream() {
        return this.userService.findAll()
                .delayElements(Duration.ofSeconds(1)); //每秒返回一条数据，模拟流式响应
    }
}
