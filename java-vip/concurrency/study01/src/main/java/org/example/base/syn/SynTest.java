package org.example.base.syn;

/**
 *类说明：synchronized关键字的使用方法
 */
public class SynTest {

	private long count =0;
	private Object obj = new Object();//作为一个锁

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	/*无锁时*/
	public void incCountNoSyn(){
		count++;
	}

	/*用在同步块上*/
	public void incCountBlock(){
		synchronized (this){
			count++;
		}
	}

	/*syn直接放在方法上*/
	public synchronized void incCountMethod(){
			count++;
	}

	/*用在同步块上，但是锁的是单独的对象实例*/
	public void incCountObj(){
		synchronized (obj){
			count++;
		}
	}

	//线程
	private static class Count extends Thread{
		private SynTest simplOper;
		public Count(SynTest simplOper) {
			this.simplOper = simplOper;
		}

		@Override
		public void run() {
			for(int i=0;i<10000;i++){
				simplOper.incCountObj();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		SynTest simplOper = new SynTest();
		//启动两个线程
		Count count1 = new Count(simplOper);
		Count count2 = new Count(simplOper);
		count1.start();
		count2.start();
		Thread.sleep(50);
		System.out.println(simplOper.count);//20000
	}
}
