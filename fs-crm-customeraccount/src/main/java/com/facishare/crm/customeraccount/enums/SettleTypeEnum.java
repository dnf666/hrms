package com.facishare.crm.customeraccount.enums;

import com.facishare.paas.appframework.core.exception.ValidateException;
import org.apache.commons.lang.StringUtils;

public enum SettleTypeEnum {
    Prepay("预付", "1", false), Cash("现付", "2", false), Credit("赊销", "3", false);

    private String label;
    private String value;
    private Boolean notUsable;

    SettleTypeEnum(String label, String value, Boolean notUsable) {
        this.label = label;
        this.value = value;
        this.notUsable = notUsable;
    }

    public static SettleTypeEnum getByValue(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new ValidateException("传入的枚举类型value为空");
        }
        for (SettleTypeEnum settleTypeEnum : SettleTypeEnum.values()) {
            if (settleTypeEnum.getValue().equals(value)) {
                return settleTypeEnum;
            }
        }
        return null;
    }

    public boolean equals(SettleTypeEnum settleTypeEnum) {
        return this.getValue().equals(settleTypeEnum.getValue());
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

}
