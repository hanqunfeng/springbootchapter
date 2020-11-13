package com.example.oauth2authserverdemo.controller;

import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/12 16:13.
 */
@Slf4j
@Controller
@SessionAttributes({"authorizationRequest"})
public class OAuth2Controller {

    /**
     * 自定义授权页面，注意：一定要在类上加@SessionAttributes({"authorizationRequest"})
     *
     * @param model   model
     * @param request request
     * @return String
     * @throws Exception Exception
     */
    @RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) {

        List<String> scopeList = new ArrayList<>();

        String scope = request.getParameter("scope");
        if (scope != null) {
            String[] split = scope.split(" ");
            for(String s:split) {
                scopeList.add(s);
            }
        }


        model.put("scopeList", scopeList);
        return "oauth2/confirm_access";
    }

    /**
     * <h2>自定义登录</h2>
     * Created by hanqf on 2020/11/13 10:13. <br>
     *
     * @return java.lang.String
     * @author hanqf
     */
    @RequestMapping("/oauth/login")
    public String login(Model model,@Nullable Boolean error) {

        if(error!=null){
            model.addAttribute("error",error);
        }
        return "oauth2/login";
    }

}
