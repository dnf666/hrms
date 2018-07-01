package com.facishare.crm.deliverynote.enums;

import java.util.Objects;

/**
 * 产品权限
 * Created by chenzs on 2018/1/30.
 */
public enum ProductFunctionNumberEnum {
    VIEW_LIST("16001", "查看列表"),
    VIEW_DETAIL("16002", "查看详情"),
    CREATE("16003", "新建")
    ;

    private final String functionNumber;
    private final String displayName;

    ProductFunctionNumberEnum(String functionNumber, String displayName) {
        this.functionNumber = functionNumber;
        this.displayName = displayName;
    }

    public static ProductFunctionNumberEnum getByCode(String functionNumber) {
        for (ProductFunctionNumberEnum srcType : values()) {
            if (Objects.equals(functionNumber, srcType.functionNumber)) {
                return srcType;
            }
        }
        throw new IllegalArgumentException("functionNumber error");
    }

    public String getFunctionNumber() {
        return functionNumber;
    }

    public String getDisplayName() {
        return displayName;
    }
}