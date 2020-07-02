package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 * Created by hanqf on 2020/7/2 11:37.
 */

@RestController
public class DemoController {

    @RequestMapping("/demo")
    public String demo() {
        return "hello world 123";
    }
}
