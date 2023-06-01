package org.example.learnjuc.designpattern.twophasetermination;

/**
 * @author  Fox
 * 中断机制
 */
public class ThreadInterruptTest {

    static int i = 0;

    public static void main(String[] args)  {
        System.out.println("begin");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public  void run() {
                //中断标志位判断
                while (true) {
                    i++;
                    System.out.println(i);
                    try {
                        Thread.sleep(5000); //Thread.sleep会清除中断标志位
                    } catch (InterruptedException e) {
                        System.out.println("线程被中断抛出中断异常，可以执行退出逻辑");
                        e.printStackTrace();
                        //重新标记
                        //Thread.currentThread().interrupt();
                    }finally {

                        // 关闭资源
                    }
                    //Thread.interrupted()  会清除中断标志位
                    //Thread.currentThread().isInterrupted() 不会清除中断标志位
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("=========");
                        break;
                    }
                    if(i==10){
                        break;
                    }
                }
            }
        });

        t1.start();
        //不会停止线程t1,只会设置一个中断标志位 flag=true
        t1.interrupt();

    }
}
