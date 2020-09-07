package com.example.springsecuritydemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 * Created by hanqf on 2020/8/10 10:26.
 */

@RestController
public class DemoController {

    @PreAuthorize("hasRole('admin')")
    @GetMapping({"/","/index"})
    public String index(){
        return "hello world!";
    }
}
