package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/4/2
 */
public class AddOrUpdateFieldModel {

    @Data
    public static class Result {
        Boolean isSuccess = true;
        String message = "OK";
    }
}
