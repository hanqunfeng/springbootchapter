package com.example.springsecuritydemo.controller;

import com.example.springsecuritydemo.common.AuthenticationUtil;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * <p></p>
 * Created by hanqf on 2020/8/10 10:26.
 */

@RestController
public class DemoController {

    /**
     * @PreAuthorize("hasRole('admin')")
     * 需要具有指定角色才能执行该方法，可以声明在任意方法上，比如service的业务方法
     *
     * 如果配置类中的设置验证通过，但是该注解设置的权限验证不通过，该方法也不可以执行，
     * 简单理解就是，配置类中定义的权限是针对url的，而该注解是针对方法的，两者没有关联
    */
    @PreAuthorize("hasRole('admin')")
    @GetMapping({"/"})
    public String index(HttpServletRequest request){
        //传递不过来啊
        Principal principal  = request.getUserPrincipal();
        if(principal instanceof AttributePrincipal){
            //cas传递过来的数据
            Map<String,Object> result =( (AttributePrincipal)principal).getAttributes();
            for(Map.Entry<String, Object> entry :result.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                System.out.printf("%s:%s\r\n",key,val);
            }
        }
        return "hello world! " + AuthenticationUtil.getUsername();
    }

    @GetMapping({"/get/{id}"})
    public String getId(@PathVariable String id){
        return "id==" + id;
    }

    @GetMapping({"/demo"})
    public String demo(){
        return "demo " + AuthenticationUtil.getUsername();
    }


    @RequestMapping({"/accessDenied"})
    public String accessDenied(){
        return "accessDenied";
    }

    @RequestMapping({"/sameLogin"})
    public String sameLogin(){
        return "sameLogin";
    }

    @RequestMapping({"/fail"})
    public String fail(){
        return "fail";
    }
}
