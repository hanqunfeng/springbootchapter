package org.example;/**
 * Created by hanqf on 2020/3/3 16:38.
 */


import org.example.proxy.ProxyBean;
import org.example.proxy.service.HelloService;
import org.example.proxy.service.HelloServiceImpl;
import org.example.proxy.util.MyInterceptor;

/**
 * @author hanqf
 * @date 2020/3/3 16:38
 */
public class App {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        HelloService proxy = (HelloService) ProxyBean.getProxyBean(helloService,new MyInterceptor());
        //正常返回结果
        proxy.sayHello("hanqf");
        //抛出异常打印
        proxy.sayHello(null);
    }
}
