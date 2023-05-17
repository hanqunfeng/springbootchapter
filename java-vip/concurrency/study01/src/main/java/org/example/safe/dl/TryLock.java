package org.example.safe.dl;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *@author Mark老师   图灵学院 https://www.tulingxueyuan.cn/
 *类说明：演示普通账户的死锁和解决
 */
public class TryLock {
    private static Lock No13 = new ReentrantLock();//第一个锁
    private static Lock No14 = new ReentrantLock();//第二个锁

    //先尝试拿No13 锁，再尝试拿No14锁，No14锁没拿到，连同No13 锁一起释放掉
    private static void zhouYuDo() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        Random r = new Random();
        while(true){
            if(No13.tryLock()){
                System.out.println(threadName +" get 13");
                try{
                    if(No14.tryLock()){
                        try{
                            System.out.println(threadName  +" get 14");
                            System.out.println("zhouYuDo do work------------");
                            break;
                        }finally{
                            No14.unlock();
                        }
                    }
                }finally {
                    No13.unlock();
                }

            }
            //Thread.sleep(r.nextInt(3));
        }
    }

    //先尝试拿No14锁，再尝试拿No13锁，No13锁没拿到，连同No14锁一起释放掉
    private static void monkeyDo() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        Random r = new Random();
        while(true){
            if(No14.tryLock()){
                System.out.println(threadName +" get 14");
                try{
                    if(No13.tryLock()){
                        try{
                            System.out.println(threadName +" get 13");
                            System.out.println("monkeyDo do work------------");
                            break;
                        }finally{
                            No13.unlock();
                        }
                    }
                }finally {
                    No14.unlock();
                }

            }
            //Thread.sleep(r.nextInt(3));
        }
    }

    private static class ZhouYu extends Thread{

        private String name;

        public ZhouYu(String name) {
            this.name = name;
        }

        public void run(){
            Thread.currentThread().setName(name);
            try {
                zhouYuDo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Thread.currentThread().setName("Monkey");
        ZhouYu zhouYu = new ZhouYu("ZhouYu");
        zhouYu.start();
        try {
            monkeyDo();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
