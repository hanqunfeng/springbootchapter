package org.example.learnjuc.jmm;


import lombok.extern.slf4j.Slf4j;


/**
 * @author Fox
 *
 * -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp
 * 将hsdis-amd64.dll放在 $JAVA_HOME/jre/bin/server 目录下
 *  可见性案例
 */
@Slf4j
public  class VisibilityTest {

    // volatile   -> lock addl $0x0,(%rsp)
    private volatile boolean flag = true;
    //private volatile int count;


    public  synchronized void refresh() {
        // 希望结束数据加载工作
        flag = false;
        System.out.println(Thread.currentThread().getName() + "修改flag:"+flag);
    }

    public void load() {
        System.out.println(Thread.currentThread().getName() + "开始执行.....");
        while (flag) {
            //TODO  业务逻辑：加载数据
            //shortWait(10000);
            //synchronized可以保证可见性
            //System.out.println("正在加载数据......");
           // count++;
            //添加一个内存屏障   可以保证可见性
            //UnsafeFactory.getUnsafe().storeFence();
//            try {
//                Thread.sleep(0);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            //Thread.yield(); //让出cpu使用权

        }

        System.out.println(Thread.currentThread().getName() + "数据加载完成，跳出循环");
    }


    public static void main(String[] args) throws InterruptedException {
        VisibilityTest test = new VisibilityTest();


        // 线程threadA模拟数据加载场景
        Thread threadA = new Thread(() -> test.load(), "threadA");
        threadA.start();

        // 让threadA先执行一会儿后再启动线程B
        Thread.sleep(1000);

        // 线程threadB通过修改flag控制threadA的执行时间，数据加载可以结束了
        Thread threadB = new Thread(() -> test.refresh(), "threadB");
        threadB.start();

    }

    public static void shortWait(long interval) {
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (start + interval >= end);
    }


}
