package org.example.safe.dl;

/**
 *@author Mark老师
 *
 *类说明：不会产生死锁
 */
public class SafeOperate {

    private static Object No13 = new Object();//第一个锁
    private static Object No14 = new Object();//第二个锁
    private static Object tieLock = new Object();//第三把锁

    public void transfer(Object first,Object second)
            throws InterruptedException {

        int firstHash = System.identityHashCode(first);
        int secondHash = System.identityHashCode(second);

        if(firstHash<secondHash){
            synchronized (first){
                System.out.println(Thread.currentThread().getName()+" get "+first);
                Thread.sleep(100);
                synchronized (second){
                    System.out.println(Thread.currentThread().getName()+" get "+second);
                }
            }
        }else if(secondHash<firstHash){
            synchronized (second){
                System.out.println(Thread.currentThread().getName()+" get"+second);
                Thread.sleep(100);
                synchronized (first){
                    System.out.println(Thread.currentThread().getName()+" get"+first);
                }
            }
        }else{
            synchronized (tieLock){
                synchronized (first){
                    synchronized (second){
                        System.out.println(Thread.currentThread().getName()+" get"+first);
                        System.out.println(Thread.currentThread().getName()+" get"+second);
                    }
                }
            }
        }
    }
}
