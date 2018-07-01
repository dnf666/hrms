package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;
import lombok.ToString;

public class InvalidModel {

    @Data
    @ToString
    public static class Arg {
        private String id;
        private String lifeStatus;//有可能是invalid或者InChange(作废审批流标志)
        private String apiName;
    }

    @Data
    public static class Result {
        ObjectDataDocument objectData;
    }

    @Data
    public static class Results {

        List<ObjectDataDocument> objectData;
    }
}
