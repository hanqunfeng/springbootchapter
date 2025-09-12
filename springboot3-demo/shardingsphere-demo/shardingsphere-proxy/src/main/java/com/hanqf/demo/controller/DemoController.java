package com.hanqf.demo.controller;

import com.hanqf.demo.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by hanqf on 2025/9/9 16:14.
 */

@RestController
public class DemoController {

    @Autowired
    public AddressService addressService;

    @RequestMapping("/test")
    public String test() {
        addressService.testTransaction();
        return "success";
    }
}
