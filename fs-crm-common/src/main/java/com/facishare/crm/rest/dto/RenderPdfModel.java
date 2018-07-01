package com.facishare.crm.rest.dto;

import lombok.Data;

@Data
public class RenderPdfModel {
    @Data
    public static class Arg {
        private String objDescApiName;
        private String dataId;
        private String orientation;  //横向纵向
    }

    @Data
    public static class Result {
        private int code;
        private String msg;
        private String result;
    }
}