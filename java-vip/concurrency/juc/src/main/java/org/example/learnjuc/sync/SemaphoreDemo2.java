package org.example.learnjuc.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 实现连接池
 */
public class SemaphoreDemo2 {

    final static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        final ConnectPool pool = new ConnectPool(2);

        //5个线程并发来争抢连接资源
        for (int i = 0; i < 5; i++) {
            final int id = i + 1;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Connect connect = null;
                    try {
                        System.out.println("线程" + id + "等待获取数据库连接");
                        connect = pool.openConnect();
                        System.out.println("线程" + id + "已拿到数据库连接:" + connect);
                        //进行数据库操作2秒...然后释放连接
                        Thread.sleep(2000);
                        System.out.println("线程" + id + "释放数据库连接:" + connect);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        pool.releaseConnect(connect);
                    }

                }
            });
        }
    }
}

//数据库连接池
class ConnectPool {
    private int size;
    private Connect[] connects;

    //记录对应下标的Connect是否已被使用
    private boolean[] connectFlag;
    //信号量对象
    private Semaphore semaphore;

    /**
     * size:初始化连接池大小
     */
    public ConnectPool(int size) {
        this.size = size;
        semaphore = new Semaphore(size, true);
        connects = new Connect[size];
        connectFlag = new boolean[size];
        initConnects();//初始化连接池
    }

    private void initConnects() {
        for (int i = 0; i < this.size; i++) {
            connects[i] = new Connect();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     * @throws InterruptedException
     */
    public Connect openConnect() throws InterruptedException {
        //得先获得使用许可证，如果信号量为0，则拿不到许可证，一直阻塞直到能获得
        semaphore.acquire();
        return getConnect();
    }

    private synchronized Connect getConnect() {
        for (int i = 0; i < connectFlag.length; i++) {
            if (!connectFlag[i]) {
                //标记该连接已被使用
                connectFlag[i] = true;
                return connects[i];
            }
        }
        return null;
    }

    /**
     * 释放某个数据库连接
     */
    public synchronized void releaseConnect(Connect connect) {
        for (int i = 0; i < this.size; i++) {
            if (connect == connects[i]) {
                connectFlag[i] = false;
                semaphore.release();
            }
        }
    }
}

/**
 * 数据库连接
 */
class Connect {

    private static int count = 1;
    private int id = count++;

    public Connect() {
        //假设打开一个连接很耗费资源，需要等待1秒
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("连接#" + id + "#已与数据库建立通道！");
    }

    @Override
    public String toString() {
        return "#" + id + "#";

    }

}
