package com.facishare.crm.electronicsign.enums.type;

import java.util.Optional;

public enum SignerTypeEnum {
    CRM_ACCOUNT(1, "CRM客户"),
    TENANT(2, "租户")
    ;

    private int type;
    private String message;

    SignerTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<SignerTypeEnum> get(int type) {
        for (SignerTypeEnum typeEnum : SignerTypeEnum.values()) {
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