package com.facishare.crm.customeraccount.util;

import com.facishare.crm.customeraccount.exception.ArgumentException;

/**
 * Created by xujf on 2017/9/28.
 */
public class AssertUtil {

    public static void argumentNotNullOrEmpty(String argument, Object obj) {
        if (obj == null) {
            throw new ArgumentException(argument, "is null");
        }
        if (obj instanceof String) {
            if (((String) obj).isEmpty()) {
                throw new ArgumentException(argument, "is empty");
            }
        }
    }
}
