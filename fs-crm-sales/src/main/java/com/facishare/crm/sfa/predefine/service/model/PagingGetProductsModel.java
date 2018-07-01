package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by luxin on 2017/11/15.
 */
public interface PagingGetProductsModel {

    @Data
    class Arg {
        @JSONField(name = "M1")
        @NotEmpty(message = "priceBookId is blank")
        private String priceBookId;

        @JSONField(name = "M2")
        private String barCode;

        @JSONField(name = "M3")
        private String name;

        @JSONField(name = "M4")
        private String category;

        @JSONField(name = "M5")
        private Integer offset = 0; //默认为0

        @JSONField(name = "M6")
        private Integer limit = 2; //默认分组大小是2

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
