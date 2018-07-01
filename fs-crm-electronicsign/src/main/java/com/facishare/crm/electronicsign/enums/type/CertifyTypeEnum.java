package com.facishare.crm.electronicsign.enums.type;

import java.util.Optional;

public enum CertifyTypeEnum {
    INDIVIDUAL(1, "个人"),
    ENTERPRISE(2, "企业");

    private int type;
    private String message;

    CertifyTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<CertifyTypeEnum> get(int type) {
        for (CertifyTypeEnum typeEnum : CertifyTypeEnum.values()) {
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