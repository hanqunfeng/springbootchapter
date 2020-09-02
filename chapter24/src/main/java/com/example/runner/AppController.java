package com.example.runner;/**
 * Created by hanqf on 2020/4/2 16:18.
 */


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hanqf
 * @date 2020/4/2 16:18
 *
 * headers : 数组。请求头参数中必须含有指定的字段，比如：Content-Type=application/json;charset=utf8
 * version=1.0 含有字段和值与之相同
 * version 含有字段即可
 * !version 不含有字段才可以访问
 * !version=1.0 必须含有字段名称，但值不相等
 *
 * produces : 数组。它的作用是指定返回值类型，不但可以设置返回值类型还可以设定返回值的字符编码，例如application/json;charset=utf-8
 *
 * consumes : 数组。指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
 */
@Controller
@RequestMapping("/app")
public class AppController {

    @GetMapping("/index")
    public String index(Model model){
        System.out.println("index==========");
        return "common/index";
    }
}
