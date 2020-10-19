package com.example.chapter40.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <h1>获取sessionId</h1>
 * 相同域名或IP下，session id相同
 * Created by hanqf on 2020/10/19 17:21.
 */

@RestController
public class SessionController {

    @GetMapping("/sessionId")
    public String getSessionId(HttpSession session){
        return "Session ID:" + session.getId();
    }
}
