package com.example.runner;


import com.example.externalproperties.PropertiesDemo;
import com.example.utils.ExternalPropertiesRefresh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>index</p>
 * Created by hanqf on 2020/4/16 22:36.
 */

@RestController
public class IndexController {
    @Autowired
    private ExternalPropertiesRefresh externalPropertiesRefresh;

    @Autowired
    private PropertiesDemo propertiesDemo;

    @RequestMapping("/")
    public String index() {
        return "hello world";
    }

    @RequestMapping("/refresh")
    public String refreshpro() {
        externalPropertiesRefresh.refresh();
        return "refresh properties success";
    }

    @RequestMapping("/{beanName}/refresh")
    public String refreshProByBeanName(@PathVariable String beanName) {
        externalPropertiesRefresh.refresh(beanName);
        return "refresh properties success for " + beanName;
    }

    @RequestMapping("/demo")
    public PropertiesDemo demo() {
        return propertiesDemo;
    }

}
