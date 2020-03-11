package org.example.proxy.service;/**
 * Created by hanqf on 2020/3/3 16:40.
 */


import org.springframework.util.StringUtils;

/**
 * @author hanqf
 * @date 2020/3/3 16:40
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        if(StringUtils.hasText(name)){
            System.out.println("hello " + name);
        }else {
            throw  new RuntimeException("ERROR");
        }
    }
}
