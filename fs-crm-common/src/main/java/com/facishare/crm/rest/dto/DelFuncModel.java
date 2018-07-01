package com.facishare.crm.rest.dto;

import java.util.List;

import com.facishare.paas.appframework.privilege.dto.AuthContext;

import lombok.Data;

public class DelFuncModel {
    @Data
    public static class Arg {
        private AuthContext authContext;
        private List<String> funcSet;
    }

    @Data
    public static class Result {
        private int errCode;
        private String errMessage;
        private Object result;
        private boolean success;
    }
}
