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
