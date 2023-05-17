package org.example.base.abc;

/**
 *类说明：StartAndRun在执行上的区别
 */
public class StartAndRun {
    public static class ThreadRun extends Thread{

        @Override
        public void run() {
            System.out.println("I am "+Thread.currentThread().getName());
            int i = 90;
            while(i>0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                System.out.println("I am "+Thread.currentThread().getName()
                        +" and now the i="+i--);
            }
        }
    }

    public static void main(String[] args) {
        ThreadRun threadRun = new ThreadRun();
        threadRun.setName("threadRun");
        threadRun.start();
        //threadRun.run();
       // threadRun.run();
    }
}
