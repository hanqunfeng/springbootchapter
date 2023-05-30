package org.example.learnjuc.jmm;


//import com.tuling.learnjuc.factory.UnsafeFactory;

/**
 * @author  Fox
 *
 */
public class ReOrderTest {

    private static  int x = 0, y = 0;
    private  static  int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int i=0;
        while (true) {
            i++;
            x = 0;
            y = 0;
            a = 0;
            b = 0;

            /**
             *  x,y的值是多少:  0,1 1,0  1,1  0,0
             */
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    //用于调整两个线程的执行顺序
                    shortWait(20000);
                    a = 1; //volatile 写
                    // 内存屏障StoreLoad   lock; addl $0,0(%%rsp)
                    //UnsafeFactory.getUnsafe().storeFence();
                    x = b; //volatile 读

                }
            });
            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    b = 1;
                    y = a;

                }
            });

            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();

            System.out.println("第" + i + "次（" + x + "," + y + ")");
            if (x==0&&y==0){
                break;
            }
        }
    }

    public static void shortWait(long interval){
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while(start + interval >= end);
    }

}
