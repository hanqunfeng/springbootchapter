package com.example.swagger3demo.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>DemoController</p>
 * Created by hanqf on 2020/10/9 12:47.
 */

@RestController
@RequestMapping("/demo")
public class DemoController {

    @ApiOperation(value = "demo",notes = "swagger api demo")
    @GetMapping("/demoMap")
    public Map<String,String> demoMap(){
        Map<String, String> map = new HashMap<>();
        map.put("name","张三");
        map.put("age","33");
        return map;
    }
}
