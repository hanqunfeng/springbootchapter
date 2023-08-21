package com.example.controller;

import com.example.common.response.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>DemoController</h1>
 * Created by hanqf on 2023/8/21 14:19.
 */


@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/hello")
    public AjaxResponse hello(){
        return AjaxResponse.success("hello");
    }
}
