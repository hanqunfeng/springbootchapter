package com.example.log4j2demo.controller;


import com.example.log4j2demo.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2020/10/21 10:08.
 */

@RestController
@Slf4j
@RequestMapping("/demo")
public class DemoController {


    @GetMapping("/index")
    public CommonResponse index(){
        log.trace("DemoController trace");
        log.debug("DemoController debug");
        log.info("DemoController info");
        log.warn("DemoController warn");
        log.error("DemoController error");
        int i = 1 / 0;
        return CommonResponse.success();
    }


}
