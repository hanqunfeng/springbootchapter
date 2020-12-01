package com.example.oauth2clientwebfluxdemo.controller;

import com.example.oauth2clientwebfluxdemo.security.CustomServerOAuth2AuthorizedClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>login</h1>
 * Created by hanqf on 2020/11/30 18:02.
 */

@Controller
public class AuthController {

    @Autowired
    ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

    @Autowired
    private CustomServerOAuth2AuthorizedClientRepository customServerOAuth2AuthorizedClientRepository;

    @GetMapping("/login")
    public String login(Model model){
        Map<String, String> map = new HashMap<>();
        if(reactiveClientRegistrationRepository instanceof InMemoryReactiveClientRegistrationRepository){
            ((InMemoryReactiveClientRegistrationRepository)reactiveClientRegistrationRepository).forEach(registrations->{
                String registrationId = registrations.getRegistrationId();
                String clientName = registrations.getClientName();
                System.out.println(registrationId + "---" + clientName);
                map.put(registrationId,clientName);
            });
        }

        model.addAttribute("registrations", map);
        return "oauth2/login";
    }

    //@GetMapping("/logout")
    //public String logout(Model model,Authentication principal, ServerWebExchange exchange){
    //    String authorizedClientRegistrationId = null;
    //    if(principal instanceof OAuth2AuthenticationToken){
    //        authorizedClientRegistrationId = ((OAuth2AuthenticationToken) principal).getAuthorizedClientRegistrationId();
    //    }
    //    customServerOAuth2AuthorizedClientRepository.removeAuthorizedClient(authorizedClientRegistrationId,principal,exchange).subscribe();
    //    return login(model);
    //}
}
