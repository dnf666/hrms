package com.facishare.crm.erpstock.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author linchf
 * @date 2018/4/18
 */
public class CommonThreadPoolUtils {
    private static Logger logger = LoggerFactory.getLogger(CommonThreadPoolUtils.class);

    private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    //Create customized thread.
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(), new BasicThreadFactory.Builder().namingPattern("schedule-thread-%d")
                    .uncaughtExceptionHandler((t, e) -> logger.warn("Thread {} was terminated.", t.getName(), e))
                    .priority(Thread.NORM_PRIORITY).daemon(false).build());

    private CommonThreadPoolUtils() {
    }

    public static Executor getExecutor(){
        return executor;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}

