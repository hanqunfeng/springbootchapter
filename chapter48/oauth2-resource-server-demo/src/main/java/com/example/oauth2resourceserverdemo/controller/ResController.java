package com.example.oauth2resourceserverdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>res</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
@RequestMapping(value = "/res")
public class ResController {

    @GetMapping("/res1")
    public String res1(){
        return "res1";
    }

    @PostMapping("/res2")
    public String res2(){
        return "res2";
    }

}
