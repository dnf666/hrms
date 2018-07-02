package com.facishare.crm.deliverynote.predefine.service.dto;

import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.crm.deliverynote.predefine.model.LogisticsVO;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by chenzs on 2018/1/9.
 */
@Data
public class DeliveryNoteType {
    /**
     * 查询"发货单开关"是否开启
     */
    @Data
    public static class IsDeliveryNoteEnableResult {
        private boolean enable;
        private boolean hasSalesOrderNeedUpdate;  //是否有需要修改状态的销售订单

        /**
         * @see DeliveryNoteSwitchEnum
         */
        private int switchStatus;
    }

    /**
     * 开启"发货单开关"的结果
     */
    @Data
    public static class EnableDeliveryNoteResult {
        /**
         * @see DeliveryNoteSwitchEnum
         */
        private int enableStatus;
        private String message;
    }

    /**
     * 开启"库存"，加添字段的结果
     */
    @Data
    public static class AddFieldResult {
        /**
         * 1、失败  2、成功
         */
        private int errorCode;
        private String message;
    }

    @Data
    public static class EmptyResult {
        private String result = "OK";
    }

    /**
     * 添加字段describe、layout、data
     */
    public static class AddFieldDescribeAndDataModel {
        @Data
        public static class Result {
            private String result = "OK";
        }
    }

    @Data
    public static class ConfirmReceiveArg {
        private String deliveryNoteId;
        private String receiveRemark;
        private List<DeliveryNoteProduct> deliveryNoteProducts;
    }

    @Data
    public static class DeliveryNoteProduct {
        private String productId;
        private BigDecimal realReceiveNum;
        private String receiveRemark;
    }

    @Data
    public static class GetByDeliveryNoteIdResult {
        private ObjectDataDocument deliveryNoteObjData;
        private List<ObjectDataDocument> deliveryNoteProductObjDataList;
    }

    @Data
    public static class GetLogisticsArg {
        private String deliveryNoteId;
    }

    @Data
    @Builder
    public static class GetLogisticsResult {
        private LogisticsVO logisticsVO;
    }

    @Data
    public static class GetByDeliveryNoteIdArg {
        private String deliveryNoteId;
    }

    @Data
    public static class GetBySalesOrderIdArg {
        private String salesOrderId;

    }
    @Data
    public static class GetBySalesOrderIdResult {
        private List<ObjectDataDocument> deliveryNoteList;
        private Map<String, List<ObjectDataDocument>> deliveryNoteId2ProductsMap;
    }

    /**
     * 根据订单ID获取订单的订货仓库
     */
    public static class GetWarehouseBySalesOrderIdModel {
        @Data
        public static class Result {
            private String id;
            private String name;
        }

        @Data
        public static class Arg {
            private String salesOrderId;
        }
    }

    /**
     * 获取可发货的订单产品列表
     */
    public static class GetCanDeliverProductsModel {
        @Data
        @AllArgsConstructor
        public static class EmptyReason {
            public static final int CODE_ALL_ORDER_PRODUCT_HAS_DELIVERED = 1;
            public static final String REASON_ALL_ORDER_PRODUCT_HAS_DELIVERED = "该销售订单已完成全部发货，请重新选择其他销售订单。";
            public static final int CODE_STOCK_NO_CAN_DELIVER_PRODUCT = 2;
            public static final String REASON_CODE_STOCK_NO_CAN_DELIVER_PRODUCT = "发货仓库中无可发货的产品，请重新选择其他仓库。";
            public static final int CODE_ORDER_NO_CAN_DELIVER_PRODUCT = 3;
            public static final String REASON_CODE_ORDER_NO_CAN_DELIVER_PRODUCT = "该销售订单中未包含可发货的订单产品，请重新选择。";

            private int code;
            private String reason;

            public static EmptyReason getOrderNoCanDeliverProductReason() {
                return new EmptyReason(CODE_ORDER_NO_CAN_DELIVER_PRODUCT, REASON_CODE_ORDER_NO_CAN_DELIVER_PRODUCT);
            }

            public static EmptyReason getAllOrderProductHasDeliveredReason() {
                return new EmptyReason(CODE_ALL_ORDER_PRODUCT_HAS_DELIVERED, REASON_ALL_ORDER_PRODUCT_HAS_DELIVERED);
            }

            public static EmptyReason getStockNoCanDeliverProductReason() {
                return new EmptyReason(CODE_STOCK_NO_CAN_DELIVER_PRODUCT, REASON_CODE_STOCK_NO_CAN_DELIVER_PRODUCT);
            }
        }

        @Data
        public static class Result {
            private List<DeliveryNoteProductVO> list;
            private EmptyReason emptyReason;
        }

        @Data
        public static class Arg {
            private String salesOrderId;
            private String warehouseId;
        }
    }
}