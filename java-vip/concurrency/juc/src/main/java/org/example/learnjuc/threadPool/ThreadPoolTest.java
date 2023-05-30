package org.example.learnjuc.threadPool;

import java.util.concurrent.*;

/**
 * 作者：周瑜大都督
 */
public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {


        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 500, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200));

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
//                throw new NullPointerException()`;
            }
        });


        executor.shutdown();  // SHUT
        executor.shutdownNow();

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setUncaughtExceptionHandler((th, error) -> System.out.println(th.getName() + "出错拉:" + error.getMessage()));
            return thread;
        };

        executor.setThreadFactory(threadFactory);


//        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 200, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200),
//                new ThreadFactory() {
//                    @Override
//                    public Thread newThread(Runnable r) {
//                        Thread thread = new Thread(r);
//                        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                            @Override
//                            public void uncaughtException(Thread t, Throwable e) {
//                                System.out.println("出错拉...");
//                            }
//                        });
//                        return thread;
//                    }
//                });
//
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("...");
//                throw new NullPointerException();
//            }
//        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("...");
//            }
//        }).start();


//        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(10);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Runnable task;
//                while ((task = blockingQueue.poll()) != null) {
//                    task.run();
//                }
//            }
//        }).start();


//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        thread.start();
//
//        thread.interrupt();
    }
}
