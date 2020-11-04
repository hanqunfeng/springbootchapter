package com.example.jwtdemo.controller;

import com.example.jwtdemo.exception.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2020/11/3 16:42.
 */

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/hello")
    public AjaxResponse hello(){
        return AjaxResponse.success("hello");
    }
}
