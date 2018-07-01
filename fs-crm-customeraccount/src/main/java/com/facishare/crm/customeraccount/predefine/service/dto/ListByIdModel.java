package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

public class ListByIdModel {
    @Data
    public static class Arg extends PageArg {
        private String id;
        //根据创建时间区间过滤
        private Long createTime;
        private Long createTimeEnd;
        private String recordType;
        private String lifeStatus;
        private String incomeType;
        private String outcomeType;
        private String name;
    }

    @Data
    public static class RebateArg extends PageArg {
        private String id;
        private Long createTime;
        private Long createTimeEnd;
        private String lifeStatus;
        private String incomeType;
        private String name;
    }

    @Data
    public static class RebateOutcomeArg extends PageArg {
        private String id;
    }

    @Data
    public static class Result extends PageResult {
        private List<ObjectDataDocument> objectDatas;
    }
}
