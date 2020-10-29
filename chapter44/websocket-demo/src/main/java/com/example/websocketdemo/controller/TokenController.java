package com.example.websocketdemo.controller;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>获取token</h1>
 * Created by hanqf on 2020/10/29 17:08.
 */

@RestController
@CrossOrigin(methods = {RequestMethod.GET},origins = {"*"},allowedHeaders = {"*"})
public class TokenController {

    public static ConcurrentHashMap<String, String> tokenMap = new ConcurrentHashMap<>();

    @GetMapping("/token/{userId}")
    public String getToken(@PathVariable String userId){
        if(!tokenMap.containsKey(userId)){
          tokenMap.put(userId, UUID.randomUUID().toString());
        }
        return tokenMap.get(userId);
    }
}
