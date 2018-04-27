package com.light.io;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2018/4/13.
 */
public class ThreadPool {

    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(50,
                200,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    public static void execute(Runnable task){
        threadPoolExecutor.execute(task);
    }
}
