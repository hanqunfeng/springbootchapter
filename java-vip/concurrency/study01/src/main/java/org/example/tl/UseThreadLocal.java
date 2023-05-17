package org.example.tl;

/**
 *类说明：演示ThreadLocal的使用
 */
public class UseThreadLocal {

    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    static ThreadLocal<Integer> threadLocal2 = new ThreadLocal<>();
//    static MyThreadLocal<String> threadLocal = new MyThreadLocal<>();


    /**
     * 运行3个线程,每个线程持有自己独有的String类型编号
     */
    public void StartThreadArray(){
        Thread[] runs = new Thread[3];
        for(int i=0;i<runs.length;i++){
            new TestThread(i).start();
        }
    }

    /**
     *类说明：打印出赋予的id值，应该包含0,1,2
     */
    public static class TestThread extends Thread{
        int id;
        public TestThread(int id){
            this.id = id;
        }
        public void run() {
            String threadName = Thread.currentThread().getName();

            threadLocal.set("线程_"+id);
            if(id == 2){
                threadLocal2.set(id);//线程2才会执行
            }

            System.out.println(threadName+":"+threadLocal.get());
            System.out.println(threadName+":"+threadLocal2.get());

        }
    }

    public static void main(String[] args){
    	UseThreadLocal test = new UseThreadLocal();
        test.StartThreadArray();
    }
}
