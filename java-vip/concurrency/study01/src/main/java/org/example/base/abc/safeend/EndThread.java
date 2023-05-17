package org.example.base.abc.safeend;

/**
 *类说明：如何安全中断线程
 */
public class EndThread {

	private static class UseThread extends Thread{

		private boolean cancel;

		public UseThread(String name) {
			super(name);
		}

		public void setCancel(boolean cancel) {
			this.cancel = cancel;
		}

		@Override
		public void run() {
			String threadName = Thread.currentThread().getName();
			System.out.println(threadName+" interrrupt flag ="+isInterrupted());
			//while(!isInterrupted()){
				//Thread.sleep();

			//while(!Thread.interrupted()){
			while(true){
				System.out.println(threadName+" is running");
				System.out.println(threadName+"inner interrrupt flag ="
						+isInterrupted());
			}
			//System.out.println(threadName+" interrrupt flag ="+isInterrupted());
		}
	}

	public static void main(String[] args) throws InterruptedException {
		UseThread endThread = new UseThread("endThread");
		endThread.start();
		Thread.sleep(20);
		endThread.interrupt();//中断线程，其实设置线程的中断标识位=true

	}

}
