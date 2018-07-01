package com.facishare.crm.customeraccount.util.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NamedThreadPoolExecutor extends ThreadPoolExecutor {
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60;

    public NamedThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize) {
        super(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new NamedThreadFactory(name));
    }

}
