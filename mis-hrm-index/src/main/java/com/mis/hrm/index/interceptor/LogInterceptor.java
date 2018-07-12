package com.mis.hrm.index.interceptor;

/**
 * created by dailf on 2018/7/12
 *
 * @author dailf
 */
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class LogInterceptor {
    private static final String DEFAULT_LOG_NAME = "base-logger";
    private Logger log;
    private String logName;
    private String[] methodPrefixs;

    public LogInterceptor(String logName, String methodPrefix) {
        if (StringUtils.isEmpty(logName)) {
            logName = DEFAULT_LOG_NAME;
        }
        this.logName = logName;
        this.log = LoggerFactory.getLogger(logName);
        if (!StringUtils.isEmpty(methodPrefix)) {
            methodPrefixs = methodPrefix.split(",");
        }
    }

    public Object around(ProceedingJoinPoint point) throws Throwable {
        String methodName = point.getSignature().getName();
        String className = point.getTarget().getClass().getSimpleName();
        if (methodPrefixs != null) {
            boolean match = false;
            for (String str : methodPrefixs) {
                if (methodName.startsWith(str)) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                return point.proceed();
            }
        }
        StopWatch totalStopWatch = new StopWatch();
        totalStopWatch.start();
        Object result = null;
        try {
            result = point.proceed();
            totalStopWatch.stop();
            log.info("{}-{}, cost:{} , args:{}, result:{}", className, methodName, totalStopWatch.getTotalTimeMillis(), point.getArgs(), result);
        } catch (Throwable e) {
            totalStopWatch.stop();
            log.error("{}-{}, cost:{} , args:{}, exception={}", className, methodName, totalStopWatch.getTotalTimeMillis(), point.getArgs(), e.getClass().getName() + ":" + e.getMessage(), e);
            throw e;
        }
        return result;
    }

}

