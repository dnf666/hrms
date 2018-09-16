package com.mis.hrm.util;

import com.mis.hrm.util.exception.ServerException;

public class ServiceUtil {
    public static void checkSqlExecution(boolean flag){
        if (!flag){
            throw new ServerException();
        }
    }
}
