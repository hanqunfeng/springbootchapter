package com.example.springsecuritydemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 * Created by hanqf on 2020/8/10 10:26.
 */

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/index")
    public String index(){
        return "hello world!";
    }
}
