package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

/**
 * Created by luxin on 2018/4/25.
 */
public interface PickOnePriceBookModel {

    @Data
    class Arg {

        @NotEmpty
        @JsonProperty("account_id")
        @JSONField(name = "account_id")
        private String accountId;

        @JsonProperty("price_book_id")
        @JSONField(name = "price_book_id")
        private String priceBookId;


    }

    @Data
    @AllArgsConstructor
    class Result {
        private Map<String, Object> result;
    }


}
