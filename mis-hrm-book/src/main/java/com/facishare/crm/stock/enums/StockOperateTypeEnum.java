package com.facishare.crm.stock.enums;

/**
 * @author linchf
 * @date 2018/3/6
 */
public enum StockOperateTypeEnum {
    ADD(1, "新建"),
    EDIT(2, "编辑"),
    INVALID(3, "作废"),
    RECOVER(4, "恢复"),
    ADD_FLOW_COMPLETE(5, "新建审批结束"),
    INVALID_FLOW_COMPLETE(6, "作废审批结束"),
    EDIT_FLOW_COMPLETE(7, "编辑审批结束");

    public Integer value;
    public String label;

    StockOperateTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static StockOperateTypeEnum get(Integer value) {
        for (StockOperateTypeEnum typeEnum : values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
