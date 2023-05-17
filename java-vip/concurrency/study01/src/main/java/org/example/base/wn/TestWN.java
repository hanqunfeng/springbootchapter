package org.example.base.wn;

import org.example.tools.SleepTools;

/**
 *类说明：测试wait/notify/notifyAll
 */
public class TestWN {
    private static Express express = new Express(0,"WUHAN");

    /*检查里程数变化的线程,不满足条件，线程一直等待*/
    private static class CheckKm extends Thread{
        @Override
        public void run() {
        	express.waitKm();
        }
    }

    /*检查地点变化的线程,不满足条件，线程一直等待*/
    private static class CheckSite extends Thread{
        @Override
        public void run() {
        	express.waitSite();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for(int i=0;i<2;i++){
            new CheckSite().start();
        }
        for(int i=0;i<2;i++){
            new CheckKm().start();
        }
        SleepTools.ms(500);

        for(int i=0; i<5; i++){
            synchronized (express){
                express.change();
                express.notify();
            }
            SleepTools.ms(500);
        }
    }
}
