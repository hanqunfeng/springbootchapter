package com.example.jarwarweb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2021/5/18 16:18.
 */

@RestController
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "hello world!";
    }
}
