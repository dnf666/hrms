package com.facishare.crm.stock.enums;

/**
 * @author linchf
 * @date 2018/3/6
 */
public enum StockOperateResultEnum {
    PASS(1, "通过"),
    REJECT(2, "驳回或撤回"),
    IN_APPROVAL(3, "审批中");

    public Integer value;
    public String label;

    StockOperateResultEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static StockOperateResultEnum get(String value) {
        for (StockOperateResultEnum typeEnum : values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
