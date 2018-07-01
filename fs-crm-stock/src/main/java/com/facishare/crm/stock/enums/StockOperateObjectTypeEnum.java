package com.facishare.crm.stock.enums;

/**
 * @author linchf
 * @date 2018/3/6
 */
public enum StockOperateObjectTypeEnum {
    SALES_ORDER(1, "销售订单"),
    RETURN_ORDER(2, "退货单"),
    DELIVERY_NOTE(3, "发货单"),
    GOODS_RECEIVED_NOTE(4, "入库单"),
    REQUISITION_NOTE(5, "调拨单"),
    OUTBOUND_DELIVERY_NOTE(6, "出库单"),
    MANUAL_MODIFICATION(1000, "手动修改");


    public Integer value;
    public String label;

    StockOperateObjectTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static StockOperateObjectTypeEnum get(Integer value) {
        for (StockOperateObjectTypeEnum typeEnum : values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
