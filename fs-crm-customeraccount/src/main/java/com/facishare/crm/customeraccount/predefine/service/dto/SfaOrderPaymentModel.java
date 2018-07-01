package com.facishare.crm.customeraccount.predefine.service.dto;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by xujf on 2018/1/4.
 */
@Data
public class SfaOrderPaymentModel {

    @Data
    public static class CreateArg implements Serializable {
        String paymentId;
        /**
         * key:orderPaymentId,value:预付款返利对象明细
         */
        Map<String, CreateArgDetail> orderPaymentMap;
    }

    @Data
    public static class CreateArgDetail {
        private ObjectDataDocument prepayDetailData;
        private ObjectDataDocument rebateOutcomeDetailData;
    }

    @Data
    public static class CreateResult implements Serializable {
        String paymentId;
        Map<String, CreateResultDetail> orderPaymentMap;
    }

    @Data
    public static class CreateResultDetail {
        private ObjectDataDocument prepayDetailData;
        private List<ObjectDataDocument> rebateOutcomeDetailDatas;
    }

    /**
     * 编辑回款 会有删除回款明细 同步删除 预存款/返利<br>
     */
    @Data
    public static class EditArgNew implements Serializable {
        //key 为orderpaymentId,value:status
        Map<String, String> dataMap;
        //List<String,String> dataIds;
        //String lifeStatus;
        String approvalType;
        String paymentId;
    }

    @Data
    public static class EditArg implements Serializable {
        List<String> dataIds;
        String lifeStatus;
        String approvalType;
        String paymentId;
    }

    @Data
    public static class Result {
        private boolean success;
    }

    @Data
    public static class BulkDeleteArg implements Serializable {
        Map<String, List<String>> orderPaymentMap;
        //编辑回款引起的删除对余额有变更需要额外处理，所以这里需要标识。
        String approvalType;
    }

    @Data
    public static class BulkRecoverArg implements Serializable {
        Map<String, List<String>> orderPaymentMap;

    }

    @Data
    public static class BulkInvalidArg implements Serializable {
        List<InvalidArg> invalidArgs;
    }

    @Data
    public static class InvalidArg implements Serializable {
        String lifeStatus;
        List<String> dataIds;
        String paymentId;
    }

    @Data
    public static class FlowCompleteArg implements Serializable {
        List<String> dataIds;
        String lifeStatus;
        String approvalType;
        String paymentId;
    }

    @Data
    public static class GetRelativeNameByOrderPaymentIdArg implements Serializable {
        String orderPaymentId;
    }

    @Data
    public static class GetRelativeNameByOrderPaymentIdResult implements Serializable {
        private String prepayName;
        private List<String> rebateOutcomeNames;
    }

    @Data
    public static class GetOrderPaymentCostByOrderPaymentIdArg implements Serializable {
        String orderPaymentId;
    }

    @Data
    public static class GetOrderPaymentCostByOrderPaymentIdResult implements Serializable {
        BigDecimal prepayAmount;
        BigDecimal rebateOutcomeAmount;
    }
}
