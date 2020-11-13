package com.example.oauth2clientdemo2.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>LoginController</h1>
 * Created by hanqf on 2020/11/14 00:19.
 */


@Controller
@Log4j2
public class LoginController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public LoginController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * 自定义登录页面，多租户登录时先显示该页面，由用户选择要使用的认证服务
     * @param model
     * @return
     */
    @RequestMapping("/oauth/login")
    public String login(Model model){
        Map<String,String> map = new HashMap<>();
        if(clientRegistrationRepository instanceof InMemoryClientRegistrationRepository){
            ((InMemoryClientRegistrationRepository)clientRegistrationRepository).forEach(registrations->{
                String registrationId = registrations.getRegistrationId();
                String clientName = registrations.getClientName();
                map.put(registrationId,clientName);
            });
        }
        model.addAttribute("registrations", map);
        return "oauth2/login";
    }


}

