package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;


public interface PriceBookProdResult {
    @Data
    class Arg implements Serializable {
        @JSONField(name = "M1")
        private  String pricebook_id;
        @JSONField(name = "M2")
        private String filters;
        @JSONField(name = "M3")
        private  Integer offset;
        @JSONField(name = "M4")
        private  Integer limit;
    }

    @Data
    @Builder
    class Result {
        @JSONField(
                name = "M1"
        )
        private List<ObjectDataDocument> dataList;
        @JSONField(
                name = "M2"
        )
        private Integer offset;
        @JSONField(
                name = "M3"
        )
        private Integer limit;
        @JSONField(
                name = "M4"
        )
        private Integer total;
    }
}
