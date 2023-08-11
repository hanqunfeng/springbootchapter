package com.example.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ${DESCRIPTION}
 * Created by hanqf on 2018/9/5 11:39.
 */

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalViewExceptionHandler {


    //运行时异常
    @ExceptionHandler(Exception.class) //类似于@RequestMapping(value = "/ex/{number}")修饰的方法，方法参数也可以指定
    public String runtimeExceptionHandler(RuntimeException ex, Model model) {
        log.error("",ex);
        model.addAttribute("exception",ex);
        return "common/error"; //返回视图
    }
}
