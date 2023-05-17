package org.example.safe.dl;

/**
 * @author ：Mark老师
 * @description ：不自知的死锁
 */
public class DynDeadLock {

    private static Object No1 = new Object();//第一个锁
    private static Object No2 = new Object();//第二个锁

    /*公共业务方法*/
    private static void businessDo(Object first,Object second) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        synchronized (first){
            System.out.println(threadName + " get first");
            Thread.sleep(100);
            synchronized (second){
                System.out.println(threadName + " get second");
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
                businessDo(No1,No2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //主线程，代表Monkey老师
        Thread.currentThread().setName("Monkey");
        ZhouYu zhouYu = new ZhouYu("ZhouYu");
        zhouYu.start();
        businessDo(No2,No1);
    }

}
