package com.example.clientdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <h1>SSE-demo</h1>
 * Created by hanqf on 2020/10/28 17:33.
 */

@Controller
public class ClientController {

    @GetMapping("/sse")
    public String sse_client(){
        return "sse-client";
    }

    @GetMapping("/ws")
    public String ws_client(){
        return "ws-client";
    }
}
