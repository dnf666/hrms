package com.facishare.crm.customeraccount.enums;

public enum PagingTaskEnum {

    CorrectInvalidRebateIncome(1, "把失效的返利收入扣除"), CorrectEffectiveRebateIncome(2, "把生效的返利收入加上");

    private int value;
    private String lable;

    PagingTaskEnum(int value, String label) {
        this.lable = label;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String getLable() {
        return this.lable;
    }
}
