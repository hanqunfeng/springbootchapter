package org.example.safe.dl;

/**
 *@author Mark老师   图灵学院 https://www.tulingxueyuan.cn/
 *
 *类说明：演示死锁的产生
 */
public class NormalDeadLock {

    private static Object No13 = new Object();//第一个锁
    private static Object No14 = new Object();//第二个锁

    //第一个拿锁的方法
    private static void zhouYuDo() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        synchronized (No13){
            System.out.println(threadName + " get No13");
            Thread.sleep(100);
            synchronized (No14){
                System.out.println(threadName + " get No14");
            }
        }

    }

    //第二个拿锁的方法
    private static void monkeyDo() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        synchronized (No13){
            System.out.println(threadName + " get No13");
            Thread.sleep(100);
            synchronized (No14){
                System.out.println(threadName + " get No14");
            }
        }
    }

    //子线程，代表周瑜老师
    private static class ZhouYu extends Thread{

        private String name;

        public ZhouYu(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(name);
            try {
                zhouYuDo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //主线程，代表Monkey老师
        Thread.currentThread().setName("Monkey");
        ZhouYu zhouYu = new ZhouYu("ZhouYu");
        //System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        zhouYu.start();
        monkeyDo();
    }

}
