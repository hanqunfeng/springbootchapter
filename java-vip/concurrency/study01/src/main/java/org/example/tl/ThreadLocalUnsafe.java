package org.example.tl;

import org.example.tools.SleepTools;

import java.util.Random;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 类说明：
 */
public class ThreadLocalUnsafe implements Runnable {

    //public static Number number = new Number(0);
    public static ThreadLocal<Number> value = new ThreadLocal<Number>(){
        @Override
        protected Number initialValue() {
            return new Number(0);
        }
    };

    public void run() {
        Random r = new Random();
        Number number = value.get();
        //每个线程计数加随机数
        number.setNum(number.getNum()+r.nextInt(100));
      //将其存储到ThreadLocal中
        value.set(number);
        SleepTools.ms(2);
        //输出num值
        System.out.println(Thread.currentThread().getName()+"="+value.get().getNum());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new ThreadLocalUnsafe()).start();
        }
    }

    private static class Number {
        public Number(int num) {
            this.num = num;
        }

        private int num;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "Number [num=" + num + "]";
        }
    }

}
