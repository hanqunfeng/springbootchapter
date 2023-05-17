package org.example.base.abc;

import java.util.concurrent.ExecutionException;

/**
 *类说明：守护线程的使用
 */
public class DaemonThread {
	private static class UseThread extends Thread{
		@Override
		public void run() {
			try {
				//while(!isInterrupted()){
				while (true) {
					System.out.println(Thread.currentThread().getName()
							+ " I am extends Thread.");
				}
//				System.out.println(Thread.currentThread().getName()
//						+ " interrupt flag is " + isInterrupted());
			} finally {
				//守护线程中finally不一定起作用
				System.out.println(" .............finally");
			}
		}
	}

//	static{
//		UseThread useThread = new UseThread();
//		//useThread.setDaemon(true);
//		useThread.start();
//	}

	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		UseThread useThread = new UseThread();
		useThread.setDaemon(true);
		useThread.start();
		Thread.sleep(1000);
		System.out.println("main end");
	}
}
