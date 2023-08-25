package com.example.exchange;

/**
 * <h1>ResourceServerClient</h1>
 * Created by hanqf on 2023/8/22 16:39.
 */


import com.example.response.AjaxResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange("http://localhost:8090")
public interface ResourceServerClient {

    @GetExchange("/resource")
    AjaxResponse resource();

    @GetExchange("/jwt")
    AjaxResponse jwt();


}

