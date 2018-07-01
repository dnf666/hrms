package com.facishare.crm.sfa.utilities.proxy.model;

import java.util.Map;

import lombok.Data;

/**
 * @author cqx
 * @date 2018/1/20 14:56
 */
public interface GetHomePermissionsModel {
    @Data
    class Result {
        Map<String,Object> value;
        boolean success;
        String message;
        Integer errorCode;
    }
}
