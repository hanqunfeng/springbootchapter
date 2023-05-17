package org.example.base.abc.join;


/**
 *类说明：无Join时线程的表现
 */
public class NoUseJoin {

    static class Goddess implements Runnable {
        private Thread thread;

        public Goddess(Thread thread) {
            this.thread = thread;
        }

        public Goddess() {
        }

        public void run() {
            System.out.println("Goddess开始排队打饭.....");
            try {
                if(thread!=null) thread.join();
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread().getName()
                    + " Goddess打饭完成.");
        }
    }

    static class GoddessBoyfriend implements Runnable {

        public void run() {
            System.out.println("GoddessBoyfriend开始排队打饭.....");
            System.out.println(Thread.currentThread().getName()
                    + " GoddessBoyfriend打饭完成.");
        }
    }

    public static void main(String[] args) throws Exception {

        Thread zhuGe = Thread.currentThread();
        GoddessBoyfriend goddessBoyfriend = new GoddessBoyfriend();
        Thread gbf = new Thread(goddessBoyfriend);
        Goddess goddess = new Goddess();
        Thread g = new Thread(goddess);
        gbf.start();
        g.start();
        System.out.println("zhuGe开始排队打饭.....");
        //SleepTools.second(2);//让主线程休眠2秒
        System.out.println(zhuGe.getName() + " zhuGe打饭完成.");
    }
}
