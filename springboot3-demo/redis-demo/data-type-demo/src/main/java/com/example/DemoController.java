package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by hanqf on 2026/1/16 15:15.
 */

@RestController
public class DemoController {

    @GetMapping("/api")
    public String api() {
        return "hello world";
    }
}
