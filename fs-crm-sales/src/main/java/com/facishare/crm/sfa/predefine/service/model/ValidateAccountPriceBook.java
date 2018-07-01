package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

public interface ValidateAccountPriceBook {
    @Data
    class Arg implements Serializable {
        @JSONField(name = "M1")
        private String account_id;
        @JSONField(name = "M2")
        @JsonProperty("price_book_id")
        private String priceBookId;
    }

    @Data
    @Builder
    class Result {
        @JSONField(
                name = "M1"
        )
        @Builder.Default
        private boolean result = false;
    }
}
