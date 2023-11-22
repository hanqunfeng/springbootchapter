package com.example.minanew.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author Zhang Yi
 * @date 2020-12-01.
 */
public class ThreadPools {
    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("MINA线程-pool-%d").build();
    public static ExecutorService exec = new ThreadPoolExecutor(10, 300, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
            namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
}
