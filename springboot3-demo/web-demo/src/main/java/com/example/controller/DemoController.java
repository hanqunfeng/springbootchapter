package com.example.controller;

import com.example.support.ApplicationContextProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2023/8/11 10:08.
 */

@Slf4j
@Controller
public class DemoController {

    @RequestMapping(value = {"/", "/index.do"})
    public String handleIndex(Model model, HttpServletRequest request) {
        log.info("handleIndex");
//        int a = 5/0;
        final String message = ApplicationContextProvider.getMessage("common.menu");
        model.addAttribute("message", message);
        final String supportMessage = ApplicationContextProvider.getMessage("message.cache.group.flushed", new Object[]{123});
        model.addAttribute("supportMessage",supportMessage);
        return "index";
    }
}

