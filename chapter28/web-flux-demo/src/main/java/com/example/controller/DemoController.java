package com.example.controller;

import com.example.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:56.
 */

@RestController
public class DemoController {

    @GetMapping("/demo/{id}")
    public Mono<String> demo(@PathVariable Integer id) {
        return Mono.just("Demo " + id);
    }

    @GetMapping("/user/{id}")
    //Mono代表0到1个元素的发布者
    public Mono<User> findUserById(@PathVariable Long id) {
        User user = new User();
        user.setId(id);
        user.setName("张三");
        return Mono.just(user);
    }

    @GetMapping("/users")
    //Flux代表0到N个元素的发布者
    public Flux<User> findAllUser() {
        User user = new User();
        user.setId(1L);
        user.setName("张三");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("李四");
        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user2);

        return Flux.fromStream(list.stream());
    }
}
