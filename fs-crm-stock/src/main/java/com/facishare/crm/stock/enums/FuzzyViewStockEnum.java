package com.facishare.crm.stock.enums;

/**
 * @author liangk
 * @date 13/01/2018
 */
public enum FuzzyViewStockEnum {
    STOCKOUT("1", "缺货"), SMALLAMOUNT("2", "少量"), ENOUGH("3", "充足");

    private String status;
    private String value;

    FuzzyViewStockEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }

    public String getStatus() {
        return this.status;
    }

    public String getValue() {
        return this.value;
    }
}
