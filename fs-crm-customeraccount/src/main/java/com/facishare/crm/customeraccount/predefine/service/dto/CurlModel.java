package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;
import java.util.Map;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;

import lombok.Data;

public class CurlModel {
    @Data
    public static class QueryCustomerArg {
        private int limit;
        private int offset;
        private List<String> customerIds;
    }

    @Data
    public static class QueryCustomerResult {
        private List<ObjectDataDocument> customerObjectDatas;
    }

    @Data
    public static class TenantIds {
        private List<String> tenantIds;
    }

    @Data
    public static class FixCustomerAccountLifeStatusArg {
        private List<String> customerIds;
    }

    @Data
    public static class FixCustomerAccountLifeStatusResult {
        private List<ObjectDataDocument> customerAccountObjectDatas;
    }

    @Data
    public static class FixSelectOneFieldArg {
        private String fieldApiName;
        private String objectApiName;
        private List<String> tenantIds;
    }

    @Data
    public static class FixSelectOneFieldResult {
        private ObjectDescribeDocument objectDescribe;
        private List<String> tenantIds;
    }

    @Data
    public static class UpdateLayoutArg {
        private String layoutApiName;
        private String objectApiName;
    }

    @Data
    public static class CustomerStatusBeforeInvalidArg {
        private List<String> customerIds;
    }

    @Data
    public static class CustomerStatusBeforeInvalidResult {
        Map<String, Integer> lifeStatusBeforeInvalid;
    }

    @Data
    public static class LackCustomerAccountInitResult {
        List<String> tenantIds;
    }

    @Data
    public static class ObjectApiNameArg {
        private String objectApiName;
    }

    @Data
    public static class MigrationArg {
        private String tenantId;

    }

    @Data
    public static class MigrationResult {
        private boolean success;
    }

    @Data
    public static class AddOrderPaymentFieldArg {
        private String tenantId;

    }

    @Data
    public static class AddOrderPaymentFieldResult {
        private boolean success;
    }

    @Data
    public static class DelPaymentFieldResult {
        private boolean success;
    }

    /**
     * 客户账户\预存款\返利导入功能刷权限<br>
     */
    @Data
    public static class AddImportPrivilegeArg {
        private String tenantIds; //企业id  逗号隔开
    }

    @Data
    public static class DelImportPrivilegeArg {
        private String tenantIds; //企业id  逗号隔开
    }

    /**
     * 客户账户导入功能刷权限<br>
     */
    @Data
    public static class AddImportPrivilegeResult {
        private boolean success;
    }

    public static class ListLayoutResult {
        private List<ILayout> listLayouts = Lists.newArrayList();

        public void add(ILayout listLayout) {
            listLayouts.add(listLayout);
        }
    }

    @Data
    public static class RebateIncomeIdArg {
        private List<String> rebateIncomeIds;
    }

    @Data
    public static class FixCustomerAccountBalanceArg {
        private ObjectDataDocument objectDataDocument;
    }
}
