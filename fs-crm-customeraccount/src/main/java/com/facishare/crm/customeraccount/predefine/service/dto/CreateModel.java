package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;
import java.util.Map;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

/**
 * Created by xujf on 2017/9/26.
 */
@Data
public class CreateModel {

    @Data
    public static class Arg {
        private ObjectDataDocument objectData;
    }

    @Data
    public static class Result {
        private ObjectDataDocument objectData;
    }

    @Data
    public static class ResultList {
        private List<ObjectDataDocument> objectData;
    }

    @Data
    public static class OutcomeCreateArg {
        private ObjectDataDocument objectData;
        //需要customerId查询客户账户信息
        private String customerId;
    }

}
