package com.facishare.crm.stock.enums;

/**
 * Created by linchf on 2018/1/11.
 */
public enum WarehouseIsEnableEnum {
    ENABLE("1", "启用"),

    DISABLE("2", "停用");

    public String value;
    public String label;

    WarehouseIsEnableEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static WarehouseIsEnableEnum get(String value) {
        for (WarehouseIsEnableEnum statusEnum : values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}

