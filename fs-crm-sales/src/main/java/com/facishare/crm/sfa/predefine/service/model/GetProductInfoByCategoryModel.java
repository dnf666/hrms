package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author cqx
 * @date 2018/3/13 18:35
 */
public interface GetProductInfoByCategoryModel {
    @Data
    class Arg {
        @JSONField(name = "M1")
        @NotEmpty(message = "priceBookId is blank")
        private String priceBookId;

        @JSONField(name = "M2")
        private String category;


    }

    @Data
    @AllArgsConstructor
    class Result {
        @JSONField(name = "M1")
        private List<ObjectDataDocument> dataList;

        @JSONField(name = "M2")
        private int total;
    }
}
