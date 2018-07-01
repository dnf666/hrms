package com.facishare.crm.customeraccount.enums;

public enum PrepayIncomeTypeEnum {

    OnlineCharge("线上充值", "1", false), //使用场景：通过企业钱包充值

    //    OfflineCharge("线下充值", "2", false), //使用场景：手动在页面新建预存款收入

    OrderRefund("订单退款", "3", false), //使用场景：新建退款的时候，选择退款到预存款。

    //PaymentInvalid("回款作废", "4", false), //使用场景：进行回款作废时，该回款的支付方式为预存款时。
    EBankTransfer("网银转账", "5", false),

    Check("支票", "6", false),

    WireTransfer("电汇", "7", false),

    Cash("现金", "8", false),

    Alibaba("支付宝", "9", false),

    Wechat("微信", "10", false),

    Other("其他", "other", false),;

    private String label;
    private String value;
    private Boolean notUsable;

    PrepayIncomeTypeEnum(String label, String value, Boolean notUsable) {
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
        for (PrepayIncomeTypeEnum incomeTypeEnum : values()) {
            if (incomeTypeEnum.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
