package com.facishare.crm.sfainterceptor.predefine.service.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonModel implements Serializable{
    @Data
    public static class Result {
        //无意义，满足.net的需求
        private String info = "info";
    }
}
