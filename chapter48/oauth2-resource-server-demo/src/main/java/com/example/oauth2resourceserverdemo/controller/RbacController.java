package com.example.oauth2resourceserverdemo.controller;

import com.example.oauth2resourceserverdemo.exception.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/7 01:24.
 */

@RestController
public class RbacController {

    @GetMapping("/rbac")
    public AjaxResponse demo(){
        return AjaxResponse.success("rbac");
    }
}
