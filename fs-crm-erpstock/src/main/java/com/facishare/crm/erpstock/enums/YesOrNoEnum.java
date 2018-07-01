package com.facishare.crm.erpstock.enums;

/**
 * @author linchf
 * @date 2018/4/11
 */
public enum YesOrNoEnum {
    YES(1, "是"),
    NO(2, "否");
    private int status;
    private String label;

    YesOrNoEnum(int status, String label) {
        this.status = status;
        this.label = label;
    }
    public static YesOrNoEnum valueOf(int status) {
        for (YesOrNoEnum e : values()) {
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
