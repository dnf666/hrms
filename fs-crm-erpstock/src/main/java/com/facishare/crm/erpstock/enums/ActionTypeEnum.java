package com.facishare.crm.erpstock.enums;

/**
 * @author linchf
 * @date 2018/5/14
 */
public enum ActionTypeEnum {
    SAVE(1, "新建"),
    UPDATE(2, "编辑"),
    INVALID(3, "作废"),
    QUERY(4, "查询");

    private int status;
    private String label;

    ActionTypeEnum(int status, String label) {
        this.status = status;
        this.label = label;
    }
    public static ActionTypeEnum valueOf(int status) {
        for (ActionTypeEnum e : values()) {
            if (e.getStatus() == status) {
                return e;
            }
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public int getStatus() {
        return status;
    }

    public String getStringStatus() {
        return String.valueOf(this.status);
    }
}
