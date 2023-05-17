package org.example.base.abc;

import org.example.tools.SleepTools;

import java.util.concurrent.ExecutionException;

/**
 *类说明：新启线程的方式
 */
public class NewThread {
	/*扩展自Thread类*/
	private static class UseThread extends Thread{
		@Override
		public void run() {
			super.run();
			SleepTools.second(1);
			// do my work;
			System.out.println("I am extendec Thread");
		}
	}


	/*实现Runnable接口*/
	private static class UseRunnable implements Runnable{

		@Override
		public void run() {
			// do my work;
			System.out.println("I am implements Runnable");

		}
	}


	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		UseThread useThread = new UseThread();
		useThread.start();
		//useThread.start();

		UseRunnable useRunnable = new UseRunnable();
		new Thread(useRunnable).start();
		System.out.println("main end");


	}
}
