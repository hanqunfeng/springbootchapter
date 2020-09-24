package com.example.springsecuritydemo.controller;

import com.example.springsecuritydemo.common.AuthenticationUtil;
import com.example.springsecuritydemo.common.CustomerUser;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
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
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"/"})
    public String index(HttpServletRequest request){

        //HttpSession session = request.getSession(false);
        //Assertion assertion = (Assertion)((Assertion)(session == null ? request.getAttribute("_const_cas_assertion_") : session.getAttribute("_const_cas_assertion_")));
        //Principal principal  = assertion.getPrincipal();

        //System.out.println("非springsecurity获取服务端返回属性获取方式,也就是存cas-client的配置======================");
        //Principal principal = request.getUserPrincipal();
        //if(principal instanceof AttributePrincipal){
        //    //cas传递过来的数据
        //    Map<String,Object> result =( (AttributePrincipal)principal).getAttributes();
        //    for(Map.Entry<String, Object> entry :result.entrySet()) {
        //        String key = entry.getKey();
        //        Object val = entry.getValue();
        //        System.out.printf("%s:%s\r\n",key,val);
        //    }
        //}

        //System.out.println("使用GrantedAuthorityFromAssertionAttributesUserDetailsService获取服务端返回属性获取方式======================");
        //都是角色
        //CasAuthenticationToken principal = (CasAuthenticationToken) request.getUserPrincipal();
        //UserDetails userDetails = principal.getUserDetails();
        //Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) userDetails.getAuthorities();
        //authorities.stream().forEach(System.out::println);

        System.out.println("自定义UserDetailService获取服务端返回属性获取方式======================");
        CasAuthenticationToken principal = (CasAuthenticationToken) request.getUserPrincipal();
        UserDetails userDetails = principal.getUserDetails();
        if(userDetails instanceof CustomerUser){
            Map<String, Object> map = ((CustomerUser) userDetails).getMap();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value != null) {
                    if (value instanceof List) {
                        List list = (List) value;
                        Iterator iterator = list.iterator();
                        int i = 0;
                        while (iterator.hasNext()) {
                            Object object = iterator.next();
                            System.out.println("key:" + key + ",values[" + i + "]:" + object.toString());
                            i++;
                        }
                    } else {
                        System.out.println("key:" + key + ",value:" + value.toString());
                    }
                }
            }
        }
        System.out.println(request.getRemoteUser());
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
