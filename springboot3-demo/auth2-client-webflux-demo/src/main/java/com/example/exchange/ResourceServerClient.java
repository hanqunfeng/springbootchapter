package com.example.exchange;

/**
 * <h1>ResourceServerClient</h1>
 * Created by hanqf on 2023/8/22 16:39.
 */


import com.example.response.AjaxResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;


@HttpExchange("http://localhost:8090")
public interface ResourceServerClient {

    @GetExchange("/resource")
    Mono<AjaxResponse> resource();

    @GetExchange("/jwt")
    Mono<AjaxResponse> jwt();


}

