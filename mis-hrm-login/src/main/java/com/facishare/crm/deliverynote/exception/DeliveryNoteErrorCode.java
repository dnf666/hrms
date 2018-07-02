package com.facishare.crm.deliverynote.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * errorCode
 * Created by xujf on 2017/10/11.
 */
public enum DeliveryNoteErrorCode implements ErrorCode {
    SYSTEM_ERROR(1000, "系统异常"),
    DESCRIBE_INIT_ERROR(1001, "发货单初始化异常"),
    STOCK_NOT_ENABLE(1002, "未开启库存"),
    SALES_ORDER_STATUS_NOT_NORMAL(1003, "销售订单不是已确认状态"),
    DELIVERY_NOTE_NOT_ENABLE(1004, "未开启发货单"),
    DELIVERY_NOTE_NO_PRODUCT(1005, "发货产品为空"),
    DELIVERY_NOTE_PRODUCT_NOT_IN_SALES_ORDER(1006, "发货产品不在订单内"),
    DELIVERY_NOTE_PRODUCT_NEED_GT_ZERO(1007, "发货产品数量需大于0"),
    DELIVERY_NOTE_PRODUCT_GT_ORDER_AMOUNT(1007, "产品发货数量大于订单产品数量"),
    DELIVERY_NOTE_STATUS_NOT_UN_DELIVER(1008, "发货单不是未发货状态"),
    DELIVERY_NOTE_PRODUCT_NOT_IN_STOCK(1009, "发货产品无对应库存"),
    DELIVERY_NOTE_PRODUCT_GT_REAL_STOCK(1010, "发货产品数大于实际库存"),
    LAYOUT_INFO_ERROR(1011, "layout信息有误"),
    DELIVERY_NOTE_NOT_EXISTS(1012, "发货单不存在"),
    SALES_ORDER_STATUS_NOT_HAS_DELIVERED(1013, "发货单不是已发货状态"),
    REPLACE_LAYOUT_FAILED(1014, "更新layout信息失败"),
    REPLACE_DESCRIBE_FAILED(1015, "更新describe信息失败"),
    EXIST_DELIVERED_ORDER(1016, "存在状态为已发货的订单，需要将已发货的订单改成已收货后才能开启发货单"),
    INVALID_STATUS_NOT_ALLOW_RECOVER(1017, "已作废的发货单不可恢复"),
    GET_LOGISTICS_INFO_ERROR(1018, "调用物流接口异常"),
    INVALID_EXPRESS_ORDER_ID(1019, "无效的物流单号"),
    NOT_ALL_DELIVERY_NOT_IS_INVALID(1020, "不是所有的发货单都处于'已作废'状态"),
    METADATA_QUERY_ERROR(1021, "数据查询异常"),
    EXPRESS_ORDER_ID_IS_BLANK(1022, "物流单号为空"),
    DELIVERY_NOTE_PRODUCT_STOCK_ID_INVALID(1023, "发货单产品的库存ID不合法"),
    OUT_INVOKE_RESULT_ISNULL(1024, "返回结果为空"),
    SALES_ORDER_HAS_INVALID(1025, "订单已作废"),
    QUERY_EXIST_DELIVERED_ORDER_FAILED(1026, "查询是否存在已发货状态的订单失败"),
    CREATE_FUNCODE_FOR_OBJECT_FAILED(1027, "给对象创建权限失败"),
    DELIVERY_NOTE_PRODUCT_HAS_ALL_DELIVERED(1028, "发货单产品已经完成发货"),
    OPEN_DELIVERY_NOTE_FAIL(1029, "开启发货单失败"),  //这个错误码，前端展示错误信息
    INIT_DESCRIBE_PRIVILEGE_FAILED(1030, "初始化定义、权限失败"),
    DELIVERY_NOTE_PRODUCT_UNABLE_TO_EDIT(1031, "发货单产品不可编辑"),
    INIT_PRINT_TEMPLATE_FAILED(1032, "初始化打印模板失败"),
    SALES_ORDER_INVALID_WORKFLOW_IN_PROGRESS(1033, "销售订单当前处于作废审核中"),
    WHEN_ORDER_WAREHOUSE_EMPTY_THEN_DELIVERY_WAREHOUSE_CAN_NOT_HAVE_VALUE(1034, "关联的销售订单未包含订货仓库，因此不能设置发货仓库，请将此字段留空"),
    WHEN_ORDER_WAREHOUSE_NOT_EMPTY_THEN_DELIVERY_WAREHOUSE_CAN_NOT_BE_EMPTY(1035, "发货仓库字段不能为空，请指定一个发货仓库"),
    GET_FIELD_FROM_DESCRIBE_FAILED(1036, "从对象定义获取字段失败"),
    UPDATE_FIELD_DESCRIBE_FAILED(1037, "更新字段定义失败"),
    DELIVERY_NOTE_TRANSFER_FAILED(1038, "发货单刷老数据失败"),
    DELIVERY_NOTE_PRODUCT_TRANSFER_FAILED(1039, "发货单产品刷老数据失败"),
    REAL_RECEIVE_NUM_GREAT_TO_DELIVERY_NUM(1040, "本次收货数大于发货单产品发货数"),
    DELIVERY_NOTE_PRODUCT_RECORD_TYPE_NOT_DEFAULT(1041, "发货单产品必须为预设业务类型"),
    DELIVERY_NOTE_PRODUCT_UNABLE_TO_ADD(1042, "发货单产品不可新增"),
    DELIVERY_NOTE_PRODUCT_FIELD_PRODUCT_ID_CAN_NOT_BE_BLANK(1043, "发货单产品的产品ID字段不可为空"),
    DELIVERY_NOTE_PRODUCT_FIELD_STOCK_ID_CAN_NOT_BE_BLANK(1044, "发货单产品的库存ID字段不可为空"),
    DELIVERY_NOTE_PRODUCT_REPEATED(1045, "发货单产品的不可重复"),
    BUSINESS_ERROR(1046, "业务异常"),
    UPDATE_CUSTOMER_ORDER_FOR_DELIVERY_NOTE_ERROR(1047, "发货单状态变化时同步更新订单失败"),
    Transfer_SEND_DATA_FAILED(1048, "发送数据给刷库中心失败"),
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