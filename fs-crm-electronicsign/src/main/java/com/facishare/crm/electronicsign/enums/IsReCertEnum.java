package com.facishare.crm.electronicsign.enums;

import java.util.Optional;

/**
 * 是否重新认证
 */
public enum IsReCertEnum {
    YES(1, "是"),
    NO(1, "不是");

    private int type;
    private String message;

    IsReCertEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<IsReCertEnum> get(int type) {
        for (IsReCertEnum typeEnum : IsReCertEnum.values()) {
            if (typeEnum.type == type) {
                return Optional.of(typeEnum);
            }
        }
        return Optional.empty();
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}