package com.example.controller;

import com.example.response.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <h1>RbacController</h1>
 * Created by hanqf on 2023/8/28 16:40.
 */


@RestController
public class RbacController {

    @GetMapping("/rbac")
    public Mono<AjaxResponse> rbac(){
        return Mono.just(AjaxResponse.success("rbac"));
    }
}
