package com.facishare.crm.electronicsign.enums.type;

import java.util.Optional;

/**
 * 签署方式
 */
public enum SignTypeEnum {
    By_Hand(1, "手动"),
    Auto(2, "自动")
    ;

    private int type;
    private String message;

    SignTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<SignTypeEnum> get(int type) {
        for (SignTypeEnum typeEnum : SignTypeEnum.values()) {
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