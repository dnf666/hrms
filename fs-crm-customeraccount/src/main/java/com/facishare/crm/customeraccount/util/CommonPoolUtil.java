package com.facishare.crm.customeraccount.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.facishare.crm.customeraccount.util.thread.NamedThreadPoolExecutor;

public class CommonPoolUtil {
    private static final ExecutorService pool = new NamedThreadPoolExecutor("CommonPool-thread", 1, 20);

    public static ExecutorService getPool() {
        return pool;
    }

    public static void execute(Runnable task) {
        pool.execute(task);
    }

    public static <V> Future<V> submit(Callable<V> task) {
        return pool.submit(task);
    }

}
