package com.facishare.crm.customeraccount.enums;

public enum PrepayOutcomeTypeEnum {
    OffsetOrder("抵扣订单", "1", false), //新建订单，回款支付方式为 预存款

    //RefundInvalid("退款作废", "2", false), //使用场景：退款作废，如果该退款的退款路径为退款到预存款

    ManualDeduction("手动扣减", "3", false), //使用场景：上游手动创建支出明细<br>

    Other("其他", "other", false),

    ;

    private String label;
    private String value;
    private Boolean notUsable;

    PrepayOutcomeTypeEnum(String label, String value, Boolean notUsable) {
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

    public static boolean contain(String value) {
        for (PrepayOutcomeTypeEnum outcomeTypeEnum : values()) {
            if (outcomeTypeEnum.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
