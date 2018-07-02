package com.facishare.crm.erpstock.enums;

/**
 * Created by linchf on 2018/1/11.
 */
public enum ErpWarehouseEnableEnum {
    ENABLE("1", "启用"),

    DISABLE("2", "停用");

    public String value;
    public String label;

    ErpWarehouseEnableEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static ErpWarehouseEnableEnum get(String value) {
        for (ErpWarehouseEnableEnum statusEnum : values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}

