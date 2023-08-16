/*******************************************************************************
 * COPYRIGHT Beijing cp-boss-Tech Co.,Ltd.                                      *
 *******************************************************************************
 * 源文件名: IndexController.java
 * 功能:
 * 版本:	1.0
 * 编制日期: 2009-6-8
 * 说明:
 * 修改历史: (主要历史变动原因及说明)
 * YYMMDD      |     Author    |    Change Description
 * 2014-03-15    sunchengqi         Created
 *******************************************************************************/
package com.example.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * 首页视图Controller
 *
 * @author sunchengqi
 * @version 1.0
 */
@Controller
@Slf4j
public class IndexController {

    /**
     * 登录页面
     *
     * @param model 数据封装
     * @return 登录视图
     */
    @RequestMapping("/login.do")
    public String handleLogin(Model model, String login_error, HttpServletRequest request) {

        //更新系统语言，这里这么做是因为切换语言后LocaleContextHolder不能立刻更新locale，所以这里需要特别设置一下
        //LocaleContextHolder.setLocale(cookieLocaleResolver.resolveLocale(request));

        if (StringUtils.hasText(login_error)) {
            model.addAttribute("sErrorCause", login_error);
        }

        return "common/login";
    }


    @RequestMapping("/access/denied.do")
    public String handleAccessDenied(Model model, HttpServletRequest request) {
        AccessDeniedException ex = (AccessDeniedException) request
                .getAttribute(WebAttributes.ACCESS_DENIED_403);
        String exMessage = ex.getMessage();
        if (exMessage.contains("Could not verify the provided CSRF token because your session was not found")) {
            return "common/sessionTimeout";
        }

        StringWriter sw = new StringWriter();
        model.addAttribute("errorDetails", exMessage);
        ex.printStackTrace(new PrintWriter(sw));
        model.addAttribute("errorTrace", sw.toString());
        return "common/accessDenied";
    }

    @RequestMapping("/access/sameLogin.do")
    public String handleAccessSameLogin(Model model) {

        return "common/accessSameLogin";
    }

}
