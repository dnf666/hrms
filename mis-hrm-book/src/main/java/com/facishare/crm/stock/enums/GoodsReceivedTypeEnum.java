package com.facishare.crm.stock.enums;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by linchf on 2018/1/10.
 */

public enum GoodsReceivedTypeEnum {
    PURCHASE("1", "采购入库"),

    RETURN("2", "退货入库"),

    OTHER("3", "其他入库"),

    REQUISITION("4", "调拨入库");

    public String value;
    public String label;

    GoodsReceivedTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static GoodsReceivedTypeEnum get(String value) {
        for (GoodsReceivedTypeEnum typeEnum : values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
