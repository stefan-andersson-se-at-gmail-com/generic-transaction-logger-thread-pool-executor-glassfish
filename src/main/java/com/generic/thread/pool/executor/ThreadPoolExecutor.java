package com.generic.thread.pool.executor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Stefan Andersson
 */

public class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {

    static final int defaultCorePoolSize = 5;
    static final int defaultMaximumPoolSize = 10;
    static final long defaultKeepAliveTime = 10;
    static final TimeUnit defaultTimeUnit = TimeUnit.SECONDS;
    static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue();
    private static ThreadPoolExecutor instance;

    private ThreadPoolExecutor() {
        super(defaultCorePoolSize, defaultMaximumPoolSize, defaultKeepAliveTime, defaultTimeUnit, workQueue);
    }

    synchronized static ThreadPoolExecutor getInstance() {
        if (instance == null) {
            instance = new ThreadPoolExecutor();
        }
        return instance;
    }
}
