package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.metadata.api.IObjectData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by xujf on 2017/9/26.
 */
@Data
public class PrepayTransactionType {
    @Data
    public static class EditParams {
        private String id;
        private String apiName;
        private Double amount;
    }

    @Data
    public static class InvalidParams {
        private String id;
        private String apiName;
    }

    @Data
    public static class IdParam {
        private String id;
    }

    @Data
    public static class ListByCustomerIdArg extends PageArg {
        private String customerId;

    }

    @Data
    public static class GetByPaymentIdArg {
        private String paymentId;
    }

    @Data
    public static class GetByRefundIdArg {
        private String refundId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ObjectListResult extends PageResult {
        public List<IObjectData> objectList;

        public ObjectListResult(List<IObjectData> list, Integer pageNumber, Integer pageSize, Integer totalNumber) {
            super(pageNumber, pageSize, totalNumber);
            objectList = list;
        }
    }

    @Data
    public static class ObjectDataResult {
        public IObjectData objectData;
    }

    /**
     * 参考自PrepayTransactionDetailConstants.java
     */
    @Data
    public class PrepayTransactionDetailObj {
        public String name;
        public String customer_id;
        public String customer_account_id;
        public String amount;
        public String transaction_time;
        public String payment_id;
        public String refund_id;
        public String income_type;
        public String outcome_type;
        public String online_charge_no;
    }
}
