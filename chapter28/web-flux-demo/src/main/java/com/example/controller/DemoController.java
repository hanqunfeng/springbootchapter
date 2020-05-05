package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}
