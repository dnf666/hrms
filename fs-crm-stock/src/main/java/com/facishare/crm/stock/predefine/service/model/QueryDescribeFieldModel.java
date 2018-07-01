package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author liangk
 * @date 31/05/2018
 */
public class QueryDescribeFieldModel {
    @Data
    public static class Arg {

    }


    @Data
    public static class Result<T> {
        private T data;
    }
}
