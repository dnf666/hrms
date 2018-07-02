package com.facishare.crm.aop;

import com.facishare.paas.appframework.core.exception.APPException;
import com.github.trace.aop.ServiceProfiler;

/**
 * 蜂眼监控
 * Created with IntelliJ IDEA.
 * User: yusb
 * Date: 17-9-27
 * Time: 下午2:15
 */
public class CrmServiceProfiler extends ServiceProfiler {

    @Override
    protected boolean isFail(Throwable e) {
        if (e == null)
            return false;

        if (e instanceof APPException) {
            return true;
        }
        return false;
    }

}
