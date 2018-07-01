package com.facishare.crm.rest.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

/**
 * Created by xujf on 2017/10/17.
 */
@Data
public class QueryCustomersByPage {

    @Data
    public static class Arg {
        Integer offset;
        Integer limit;
        String CustomerType;
        String Level;
        String Name;
        List<String> CustomerIDs;
    }

    @Data
    @ToString
    public static class Result {

        @Data
        public static class Page {
            @SerializedName("PageCount")
            private Integer pageCount;
            @SerializedName("TotalCount")
            private Integer totalCount;
        }

        @Data
        public static class Value {
            @SerializedName("Items")
            private List<CrmCustomerVo> items;
            @SerializedName("Page")
            private Page page;
        }

        private Value value;
        private String message;
        private Integer errorCode;

        public boolean isSuccess() {
            return errorCode != null && errorCode == 0;
        }
    }

}
