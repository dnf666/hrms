package com.facishare.crm.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * errorCode
 * Created by xujf on 2017/10/11.
 */
public enum DeliveryNoteErrorCode implements ErrorCode {
    PARAMETER_ERROR(1000, "参数错误"),
    DESCRIBE_INIT_ERROR(1001, "发货单初始化异常"),
    STOCK_NOT_ENABLE(1002, "未开启库存"),
    SALES_ORDER_STATUS_NOT_NORMAL(1003, "销售订单不是已确认状态"),
    DELIVERY_NOTE_NOT_ENABLE(1004, "未开启发货单"),
    DELIVERY_NOTE_NO_PRODUCT(1005, "发货产品为空"),
    DELIVERY_NOTE_PRODUCT_NOT_IN_SALES_ORDER(1006, "发货产品不在订单内"),
    DELIVERY_NOTE_PRODUCT_LT_ZERO(1007, "发货产品数量小于0"),
    DELIVERY_NOTE_PRODUCT_GT_ORDER_AMOUNT(1007, "产品货数量大于订单产品数量"),
    DELIVERY_NOTE_STATUS_NOT_UN_DELIVER(1008, "产品货数量大于订单产品数量"),
    DELIVERY_NOTE_PRODUCT_NOT_IN_STOCK(1009, "发货产品无对应库存"),
    DELIVERY_NOTE_PRODUCT_GT_REAL_STOCK(1010, "发货产品数大于实际库存"),
    LAYOUT_INFO_ERROR(1011, "layout信息有误"),
    DELIVERY_NOTE_NOT_EXISTS(1012, "发货单不存在"),
    SALES_ORDER_STATUS_NOT_HAS_DELIVERED(1013, "发货单不是已发货状态"),
    REPLACE_LAYOUT_FAILED(1014, "更新layout信息失败"),
    REPLACE_DESCRIBE_FAILED(1015, "更新describe信息失败"),
    QUERY_CONFIG_FAILED(1016, "查询config信息失败"),
    ;


    int code;
    String message;

    DeliveryNoteErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    DeliveryNoteErrorCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}