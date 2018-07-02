package com.facishare.crm.electronicsign.predefine.service.dto;

import com.alibaba.fastjson.JSONArray;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import lombok.Data;

import java.util.ArrayList;

@Data
public class SignRecordType {
    public static class GetContractFileAttachment {
        @Data
        public static class Result {
            private Integer status = 1;
            private String message = "success";
            private ArrayList contractFileAttachment;
        }

        @Data
        public static class Arg {
            /**
             * @see AppTypeEnum
             */
            private String appType;
            private String objApiName;
            private String objDataId;
        }
    }
}
