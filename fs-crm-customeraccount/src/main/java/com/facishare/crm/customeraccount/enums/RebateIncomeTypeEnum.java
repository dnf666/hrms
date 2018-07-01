package com.facishare.crm.customeraccount.enums;

public enum RebateIncomeTypeEnum {
    OrderRefund("订单退款", "1", false), //订单退款，选择退款到返利<br>

    SaleBonus("销售返点", "2", false), //手动

    OrderRebate("订单返利", "3", false), //6.3

    Other("其他", "other", false),;

    private String label;
    private String value;
    private Boolean notUsable;

    RebateIncomeTypeEnum(String label, String value, Boolean notUsable) {
        this.label = label;
        this.value = value;
        this.notUsable = notUsable;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public Boolean getNotUsable() {
        return notUsable;
    }
}
