package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;
import lombok.ToString;

public class SfaBulkInvalidModel {

    @Data
    @ToString
    public static class Arg {
        private String lifeStatus;
        private List<String> dataIds;
    }

    @Data
    public static class Result {
        private List<ObjectDataDocument> objectDatas;

        public Result() {
        }

        public Result(List<ObjectDataDocument> objectDatas) {
            this.objectDatas = objectDatas;
        }
    }

}
