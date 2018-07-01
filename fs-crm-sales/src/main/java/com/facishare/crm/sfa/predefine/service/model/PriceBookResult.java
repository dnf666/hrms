package com.facishare.crm.sfa.predefine.service.model;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Builder;
import lombok.Data;

public interface PriceBookResult {
    @Data
    class Arg implements Serializable {
        @JSONField(name = "M1")
        private String account_id;
        @JSONField(name = "M2")
        private Integer offset;
        @JSONField(name = "M3")
        private Integer limit;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private List<ObjectDataDocument> dataList;
        @JSONField(name = "M2")
        private Integer offset;
        @JSONField(name = "M3")
        private Integer limit;
        @JSONField(name = "M4")
        private Integer total;
    }
}
