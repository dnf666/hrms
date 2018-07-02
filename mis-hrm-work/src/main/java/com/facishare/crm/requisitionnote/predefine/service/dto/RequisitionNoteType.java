package com.facishare.crm.requisitionnote.predefine.service.dto;

import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author liangk
 * @date 15/03/2018
 */
public class RequisitionNoteType {
    public enum RequisitionSwitchEnum {
        UNABLE(0, "未开启"), FAILED(1, "开启失败"), ENABLE(2, "已经开启");
        private int status;
        private String label;

        RequisitionSwitchEnum(int status, String label) {
            this.status = status;
            this.label = label;
        }
        public static RequisitionSwitchEnum valueOf(int status) {
            for (RequisitionSwitchEnum switchStatus : values()) {
                if (switchStatus.getStatus() == status) {
                    return switchStatus;
                }
            }
            return null;
        }

        public String getLabel() {
            return label;
        }

        public int getStatus() {
            return status;
        }

        public String getStringStatus() {
            return String.valueOf(this.status);
        }
    }

    @Data
    public static class EnableRequisitionResult {
        /**
         * 0 未开启
         * 1 开启失败
         * 2 已经开启
         */
        private int enableStatus;
        private String message;
    }

    @Data
    public static class IsConfirmedResult {
        private Boolean hasConfirmed;
        private String message;
        private String goodsReceivedNoteId;
    }

    @Data
    public static class IsConfirmedArg {
        private String requisitionNoteId;
    }

    @Data
    public static class AddFieldAndDataResult {
        private int enableStatus;
        private String message;
    }

    /**
     * 调拨单正常状态编辑时主对象(调拨单)需要设定的只读字段
     */
    public static final List<String> masterReadOnlyFields = Lists.newArrayList(RequisitionNoteConstants.Field.Name.apiName,
            RequisitionNoteConstants.Field.TransferInWarehouse.apiName,
            RequisitionNoteConstants.Field.TransferOutWarehouse.apiName,
            RequisitionNoteConstants.Field.InboundConfirmed.apiName);

    /**
     * 调拨单正常状态编辑时从对象(调拨单产品)需要设定的只读字段
     */
    public static final List<String> detailsReadOnlyFields = Lists.newArrayList(RequisitionNoteProductConstants.Field.Requisition.apiName,
            RequisitionNoteProductConstants.Field.Product.apiName,
            RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName,
            RequisitionNoteProductConstants.Field.Stock.apiName,
            RequisitionNoteProductConstants.Field.AvailableStock.apiName);
}
