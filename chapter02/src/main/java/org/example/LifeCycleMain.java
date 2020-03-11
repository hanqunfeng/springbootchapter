package org.example;/**
 * Created by hanqf on 2020/3/2 12:22.
 */


import org.example.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hanqf
 * @date 2020/3/2 12:22
 */
public class LifeCycleMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        //LifeCycle lifeCycle = ctx.getBean(LifeCycle.class);
        //lifeCycle.service();
        ctx.close();
    }
}
