package org.example.base.syn;

import org.example.tools.SleepTools;

/**
 * 类说明：错误的加锁和原因分析
 */
public class TestIntegerSyn {

    public static void main(String[] args) throws InterruptedException {
        Worker worker=new Worker(1);
        //Thread.sleep(50);
        for(int i=0;i<5;i++) {
            new Thread(worker).start();
        }
    }

    private static class Worker implements Runnable{

        private Integer i;
        private Object o = new Object();

        public Worker(Integer i) {
            this.i=i;
        }

        @Override
        public void run() {
            synchronized (i) {
                Thread thread=Thread.currentThread();
                System.out.println(thread.getName()+"--@"
                        +System.identityHashCode(i));
                i++;
                System.out.println(thread.getName()+"-------[i="+i+"]-@"
                        +System.identityHashCode(i));
                SleepTools.ms(3000);
                System.out.println(thread.getName()+"-------[i="+i+"]--@"
                        +System.identityHashCode(i));
            }

        }

    }

}
