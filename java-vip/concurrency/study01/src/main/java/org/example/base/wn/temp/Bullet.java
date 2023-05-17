package org.example.base.wn.temp;

/**
 * 子弹容量
 */
public class Bullet {
    public static final Integer MAX_SIZE = 5; //子弹最多为20
    public static final Integer MIN_SIZE = 0; //子弹最多为0

    //子弹数量
    private int num ;



    //  射出子弹
    public synchronized void output() {
        try {
            while (num <= MIN_SIZE) {
                System.out.println(Thread.currentThread().getName()+" :弹夹已空,无法进行射击");
                wait();
            }
            num--;
            System.out.println(Thread.currentThread().getName()+" :弹夹有子弹,开始射击,现有子弹"+num);
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //  装入子弹
    public synchronized void input(){

        try {
            while (num >= MAX_SIZE) {
                System.out.println(Thread.currentThread().getName()+":弹夹已满,无法装入子弹");
                wait();
            }
            num++;
            System.out.println(Thread.currentThread().getName()+":弹夹未满,开始装弹,现有子弹" + num);
            notifyAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}










