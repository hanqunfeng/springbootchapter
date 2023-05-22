package org.example.base.abc.safeend;

/**
 *类说明：阻塞方法中抛出InterruptedException异常后，如果需要继续中断，需要手动再中断一次
 */
public class HasInterrputException {

	private static class UseThread extends Thread{

		public UseThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			while(!isInterrupted()) {
				try {
//					当线程在sleep()方法中被中断时，它会抛出InterruptedException异常，并且会自动清除中断状态，将中断状态重新设置为false。
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName()
							+" in InterruptedException interrupt flag is "
							+isInterrupted());
//					在catch块中调用interrupt()方法是为了重新设置中断状态。
//					当线程在sleep()方法中被中断时，它会抛出InterruptedException异常，并且会自动清除中断状态，将中断状态重新设置为false。
//					这意味着在catch块中捕获到InterruptedException异常时，中断状态已经被清除了。
//					调用interrupt()方法可以重新设置中断状态，将中断状态设置为true。
//					这样做的目的是为了确保在异常处理完成后，线程的中断状态仍然是true，以便于在后续的循环条件判断中正确检测到中断状态，从而退出循环。
					interrupt();
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName()
						+ " I am extends Thread.");
			}
			//while 循环结束后才会打印本句
			System.out.println(Thread.currentThread().getName()
					+" interrupt flag is "+isInterrupted());
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread endThread = new UseThread("HasInterrputEx");
		endThread.start();
		Thread.sleep(500);
		endThread.interrupt();


	}

}
