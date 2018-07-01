package com.facishare.crm.outbounddeliverynote.enums;

/**
 * @author linchf
 * @date 2018/3/14
 */
public enum OutboundTypeEnum {
    PRODUCE_OUTBOUND("1", "生产使用出库"),

    INVALID_OUTBOUND("2", "报废出库"),

    OUT_DATE_OUTBOUND("3", "过期退货出库"),

    TRIAL_OUTBOUND("4", "试用出库"),

    SALES_OUTBOUND("5", "销售出库"),

    REQUISITION_OUTBOUND("6", "调拨出库"),

    OTHER_OUTBOUND("7", "其他出库");

    public String value;
    public String label;

    OutboundTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static OutboundTypeEnum get(String value) {
        for (OutboundTypeEnum typeEnum : values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
