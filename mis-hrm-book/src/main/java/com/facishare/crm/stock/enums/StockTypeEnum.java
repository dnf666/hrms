package com.facishare.crm.stock.enums;

/**
 * @author linchf
 * @date 2018/5/10
 */
public enum StockTypeEnum {
    ALL_DISABLED(1, "未开启库存"),
    FS_ENABLED(2, "开启纷享库存"),
    ERP_ENABLED(3, "开启ERP库存");

    public Integer value;
    public String label;

    StockTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static StockTypeEnum get(String value) {
        for (StockTypeEnum typeEnum : values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
