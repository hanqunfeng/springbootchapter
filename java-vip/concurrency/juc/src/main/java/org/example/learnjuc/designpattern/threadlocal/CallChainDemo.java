package org.example.learnjuc.designpattern.threadlocal;
import java.util.UUID;

/**
 * 调用链
 */
public class CallChainDemo {

    private static final ThreadLocal<String> tid = new ThreadLocal<>();

    public static void main(String[] args) {

        try {
            //设置tid
            tid.set(UUID.randomUUID().toString());
            //调用服务A
            ServiceA serviceA = new ServiceA();
            serviceA.doSomething();
        }finally {
            //清除tid
            tid.remove();
        }

    }

    static class ServiceA {
        void doSomething() {
            //获取tid
            String id = tid.get();
            System.out.println("ServiceA: " + id);
            //调用服务B
            ServiceB serviceB = new ServiceB();
            serviceB.doSomething();
        }
    }

    static class ServiceB {
        void doSomething() {
            //获取tid
            String id = tid.get();
            System.out.println("ServiceB: " + id);
            //调用服务C
            ServiceC serviceC = new ServiceC();
            serviceC.doSomething();
        }
    }

    static class ServiceC {
        void doSomething() {
            //获取tid
            String id = tid.get();
            System.out.println("ServiceC: " + id);
        }
    }
}
