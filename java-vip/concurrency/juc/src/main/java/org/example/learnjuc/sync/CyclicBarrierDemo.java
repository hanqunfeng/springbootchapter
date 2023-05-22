package org.example.learnjuc.sync;

import java.util.concurrent.*;

public class CyclicBarrierDemo {
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);


    public static void main(String[] args) {


        CyclicBarrier cyclicBarrier = new CyclicBarrier(5,
                () -> System.out.println("人齐了，准备发车"));

        for (int i = 0; i < 10; i++) {
            final int id = i+1;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(id+"号马上就到");
                        int sleepMills = ThreadLocalRandom.current().nextInt(2000);
                        Thread.sleep(sleepMills);
                        System.out.println(id + "号到了，上车");
                        cyclicBarrier.await();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }catch(BrokenBarrierException e){
                        e.printStackTrace();
                    }
                }
            });
        }

    }


}
