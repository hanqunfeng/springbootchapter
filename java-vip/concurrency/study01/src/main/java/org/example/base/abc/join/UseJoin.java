package org.example.base.abc.join;


import org.example.tools.SleepTools;

/**
 *类说明：演示Join（）方法的使用
 */
public class UseJoin {

    static class Goddess implements Runnable {
        private Thread thread;

        public Goddess(Thread thread) {
            this.thread = thread;
        }

        public Goddess() {
        }

        public void run() {
            System.out.println(Thread.currentThread().getName()
                    + " Goddess开始排队打饭.....");
            try {
                if(thread!=null) thread.join();
            } catch (InterruptedException e) {
            }
            SleepTools.second(2);//休眠2秒
            System.out.println(Thread.currentThread().getName()
                    + " Goddess打饭完成.");
        }
    }

    static class GoddessBoyfriend implements Runnable {

        public void run() {
            SleepTools.second(2);//休眠2秒
            System.out.println(Thread.currentThread().getName()
                    + " GoddessBoyfriend开始排队打饭.....");
            System.out.println(Thread.currentThread().getName()
                    + " GoddessBoyfriend打饭完成.");
        }
    }

    public static void main(String[] args) throws Exception {

        Thread zhuGe = Thread.currentThread();
        GoddessBoyfriend goddessBoyfriend = new GoddessBoyfriend();
        Thread gbf = new Thread(goddessBoyfriend);
        Goddess goddess = new Goddess(gbf);
        Thread g = new Thread(goddess);
        g.start();
        gbf.start();
        System.out.println(zhuGe.getName()
                + " zhuGe开始排队打饭.....");
        g.join();
        SleepTools.second(2);//让主线程休眠2秒
        System.out.println(zhuGe.getName() + " zhuGe打饭完成.");
    }
}
