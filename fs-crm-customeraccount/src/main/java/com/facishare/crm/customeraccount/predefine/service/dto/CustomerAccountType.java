package com.facishare.crm.customeraccount.predefine.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Created by xujf on 2017/9/26.
 */
@Data
public class CustomerAccountType {

    public static enum CustomerAccountEnableSwitchStatus {
        UNABLE(0, "未开启"), OPENING(1, "开启中"), ENABLE(2, "已经开启"), FAILED(3, "开启失败"), OPENING_LATER(4, "稍后开启");

        private String label;
        private int value;

        CustomerAccountEnableSwitchStatus(int value, String label) {
            this.label = label;
            this.value = value;
        }

        public static CustomerAccountEnableSwitchStatus valueOf(int value) {
            for (CustomerAccountEnableSwitchStatus status : values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            return null;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum CreditSwitchType {
        ClOSE(0, "未开启"), OPEN(1, "开启中");

        private String label;
        private int value;

        CreditSwitchType(int value, String label) {
            this.label = label;
            this.value = value;
        }

        public static CreditSwitchType valueOf(int value) {
            for (CreditSwitchType status : values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            return null;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    @Data
    public static class GetByCustomerIdArg implements Serializable {
        String customerId;
    }

    @Data
    public static class GetByCustomerIdResult {
        private Map<String, Object> objectData;
    }

    @Data
    public static class SettleType {
        private String label;
        private String value;
        private Boolean notUsable;
    }

    @Data
    @ToString
    public static class OrderArg implements Serializable {
        String orderId;
        String customerId;
        //订单金额
        double orderAmount;
        //结算方式
        String settleType;
        double oldOrderAmount;

    }

    @Data
    public static class GetCustomerAccountAndCreditInfoResult {
        String availablePrepayAmount;
        String availableRebateAmount;
        String availableCredit;
        @JsonProperty("creditEnable")
        boolean isCreditEnable;
        String creditQuota;
        List<SettleType> settleTypeEnumList;
    }

    @Data
    public static class GetCustomerAccountAndCreditInfoArg {
        String customerId;
    }

    @Data
    public static class PaymentArg {
        String customerId;
        //不传默认是0
        double prepayToPay;
        //不传默认是0
        double rebateToPay;
    }

    @Data
    public static class CanInvalidByCustomerIdsArg {
        List<String> customerIds;
    }

    @Data
    public static class BalanceEnoughResult {
        boolean isPrepayEnough;
        boolean isRebateEnough;
    }

    /**
     * 先付情况下，则是“信用+余额”整体不足。<br>
     * 赊账情况下，则是“信用”不足。<br>
     */
    @Data
    public static class BalanceCreditEnoughResult {
        boolean isEnough;//后付的情况下需要判断
    }

    @Data
    public static class CanInvalidByCustomerIdsResult {
        //<key,value>->id
        Map<String, String> errorReasons;
        boolean success;
    }

    @Data
    public static class IsCreditEnableResult {
        boolean isEnable;
    }

    @Data
    public static class IsCustomerAccountEnableResult {
        private boolean isEnable;
    }

    @Data
    public static class GetAvailableCreditArg {
        String customerId;
    }

    @Data
    public static class GetAvailableCreditResult {
        double availableCredit;
    }

    @Data
    public static class EnableCustomerAccountResult {
        /**
         * 0 未开启
         * 1 开启中
         * 2 已经开启
         * 3 开启失败
         */
        private int enableStatus;
        private String message;
    }

    @Data
    public static class UpdateCreditSwitchArg {
        /**
         * 1.switchType: 0:关闭 1:打开
         */
        private int switchType;
    }

    @Data
    public static class EnableCreditResult {
        /**
         * 1.未开启
         * 2.开启成功
         * 3.开启失败
         */
        private boolean enableStatus;
    }

    @Data
    public static class UpdateCustomerAccountArg {
        IObjectData iObjectData;
    }

    @Data
    public static class UpdateCustomerAccountResult {
        boolean isSuccess;
    }

    @Data
    public static class CreateCustomerAccountArg {
        String customerId;
        String lifeStatus;

    }

    @Data
    public static class CreateCustomerAccountResult {
        private String customerId;
        private String customerAccountId;
    }

    @Data
    public static class BulkInitCustomerAccountArg {
        private String lifeStatus;
        private List<String> customerIds;
    }

    @Data
    public static class BatchInitCustomerAccountResult {
        boolean isSuccesss;
        List<String> failedCustomerId;
    }

    @Data
    public static class InvalidCustomerAccountArg {
        String customerId;
        String lifeStatus;

    }

    @Data
    public static class InvalidCustomerAccountResult {
        private boolean isSuccess;
        private String errorMessage;
    }

    @Data
    public static class BulkInvalidCustomerAccountArg {
        Map<String, String> customerLifeStatusMap; // key为customerId，value为lifeStatus
    }

    @Data
    public static class BulkInvalidCustomerAccountResult {
        Map<String, String> failedReasons = new HashMap<String, String>();
    }

    @Data
    public static class DeleteCustomerAccountArg {
        String customerId;
    }

    @Data
    public static class DeleteCustomerAccountResult {
        boolean isSuccess;
    }

    @Data
    public static class BulkDeleteCustomerAccountArg {
        List<String> customerIds;

    }

    @Data
    public static class BulkDeleteCustomerAccountResult {
        //Map<String, String> failedReasons = new HashMap<String, String>();//StandardDeleteAction不对结果进行返回<br。
        boolean isSuccess;
    }

    @Data
    public static class BulkCreateCustomerAccountResult {
        Map<String, String> failedReasons = new HashMap<String, String>();
    }

    @Data
    public static class LockCustomerAccountArg {
        String customerId;

    }

    @Data
    public static class LockCustomerAccountResult {

    }

    @Data
    public static class UnlockCustomerAccountArg {
        String customerId;
    }

    @Data
    public static class UnlockCustomerAccountResult {

    }

    @Data
    public static class BulkLockCustomerAccountArg {
        List<String> customerIds;

    }

    @Data
    public static class BulkUnlockCustomerAccountArg {
        List<String> customerIds;
    }

    @Data
    public static class RecoverCustomerAccountResult {
        boolean isSuccess;
    }

    @Data
    public static class RecoverCustomerAccountArg {
        String customerId;
    }

    @Data
    public static class BulkRecoverCustomerAccountArg {
        List<String> customerIds;
    }

    @Data
    public static class BulkRecoverCustomerAccountResult {
        private List<ObjectDataDocument> dataLists;

        public BulkRecoverCustomerAccountResult() {

        }

        public BulkRecoverCustomerAccountResult(List<ObjectDataDocument> dataLists) {
            this.dataLists = dataLists;
        }
    }

    @Data
    public static class CustomerRecoverArg {
        private List<String> customerIds;
    }

    @Data
    public static class CustomerRecoverResult {
        private String success = "ok";
    }

    @Data
    public static class MergeCustomerArg {
        private String mainCustomerId;
        private List<String> sourceCustomerIds;
        private Boolean relativeObjectMerge;//true--表示勾选， false--表示未勾选
    }

    @AllArgsConstructor
    @Data
    public static class MergeCustomerResult {
        private String errCode;
        private String errMessage;
    }

    @Data
    public static class RemindRecordResult {
        private Boolean success;
        private String message;
        private String errorCode;
    }

}
