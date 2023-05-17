package org.example.tl;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 类说明：
 */
public class NoThreadLocal {
    static int count;

    /**
     * 运行3个线程
     */
    public void StartThreadArray(){
        Thread[] runs = new Thread[3];
        //将i赋给线程
        for(int i=0;i<runs.length;i++){
            runs[i]=new Thread(new TestTask(i));
        }
        for(int i=0;i<runs.length;i++){
            runs[i].start();
        }
    }

    /**
     *类说明：打印出赋予的id值，应该包含0,1,2
     */
    public static class TestTask implements Runnable{
        int id;
        public TestTask(int id){
            this.id = id;
        }
        public void run() {
            count = id;
            System.out.println(Thread.currentThread().getName()
                    +" 编号为: "+count);
        }
    }

    public static void main(String[] args){
        NoThreadLocal test = new NoThreadLocal();
        test.StartThreadArray();
    }
}
