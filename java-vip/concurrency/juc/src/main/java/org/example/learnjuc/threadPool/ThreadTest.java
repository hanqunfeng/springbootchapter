package org.example.learnjuc.threadPool;

/**
 * 作者：周瑜大都督
 */
public class ThreadTest {


    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {

            boolean isStop = false;
            for (int i = 0; i < 1000000; i++) {

                if (isStop && i > 10) {
                    break;
                }

                System.out.println(i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    isStop = true;
                }
            }


        });
        t1.start();

        Thread.sleep(1000);

        t1.interrupt();

        System.out.println("end");



    }


}
