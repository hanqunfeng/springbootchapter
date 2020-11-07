package com.example.oauth2clientdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2020/11/7 13:35.
 */

@RestController
public class DemoController {
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/demo")
    public String demo(){
        return "demo";
    }
}
