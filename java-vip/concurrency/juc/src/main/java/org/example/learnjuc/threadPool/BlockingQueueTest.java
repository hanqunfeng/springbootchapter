package org.example.learnjuc.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 作者：周瑜大都督
 */
public class BlockingQueueTest {
    public static void main(String[] args) throws InterruptedException {

//        Thread.currentThread().interrupt();
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
        queue.poll(4, TimeUnit.SECONDS);

    }
}
