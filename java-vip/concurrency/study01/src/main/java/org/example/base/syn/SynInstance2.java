package org.example.base.syn;

/**
 *类说明：synchronized关键字的使用方法
 */
public class SynInstance2 {

	private static long count =0;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	/*用在同步块上*/
	public void incCountBlock(){
		synchronized (this){
			count++;
		}
	}

	//线程
	private static class Count extends Thread{
		private SynInstance2 simplOper;
		public Count(SynInstance2 simplOper) {
			this.simplOper = simplOper;
		}

		@Override
		public void run() {
			for(int i=0;i<10000;i++){
				simplOper.incCountBlock();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		SynInstance2 simplOper = new SynInstance2();
		SynInstance2 simplOper2 = new SynInstance2();
		//启动两个线程
		Count count1 = new Count(simplOper);
		Count count2 = new Count(simplOper2);
		count1.start();
		count2.start();
		Thread.sleep(50);
		System.out.println(simplOper.count);//20000
	}
}
