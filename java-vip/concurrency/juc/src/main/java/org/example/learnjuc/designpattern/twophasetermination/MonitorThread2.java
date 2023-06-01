package org.example.learnjuc.designpattern.twophasetermination;

/**
 * 用两阶段终止模式终止监控操作
 */
public class MonitorThread2 extends Thread {
    //在监控线程中添加一个volatile类型的标志变量，用于标识是否需要终止线程的执行
    private volatile boolean terminated = false;

    public void run() {
        while (!Thread.interrupted()&&!terminated) {
            // 执行监控操作
            System.out.println("监控线程正在执行监控操作...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("监控线程被中断，准备退出...");
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        // 执行清理操作
        System.out.println("监控线程正在执行清理操作...");
        releaseResources();
    }

    public void terminate() {
        //设置标志变量为true，并等待一段时间
        terminated = true;
        try {
            join(5000); // 等待5秒钟,期间监控线程会检查terminated的状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void releaseResources() {
        // 释放资源和进行必要的清理工作
        System.out.println("监控线程正在释放资源和进行必要的清理工作...");
    }

    public static void main(String[] args) throws InterruptedException {
        MonitorThread2 thread = new MonitorThread2();
        //启动监控线程
        thread.start();
        //主线程休眠期间，监控线程在执行监控操作
        Thread.sleep(10000);
        //为监控线程设置中断标志位
        thread.interrupt();
        //终止监控线程
        //thread.terminate();


        Thread.sleep(100000);
    }
}
