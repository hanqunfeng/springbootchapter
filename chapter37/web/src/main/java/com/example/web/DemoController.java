package com.example.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 * Created by hanqf on 2020/8/26 18:07.
 */

@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/index/{name}")
    public String index(@PathVariable(value = "name") String name){
        return demoService.getName(name);
    }

}
