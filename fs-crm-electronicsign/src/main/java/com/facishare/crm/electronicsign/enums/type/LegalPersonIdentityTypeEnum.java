package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum LegalPersonIdentityTypeEnum {
    Identification_card("0", "身份证");

    private String type;
    private String message;

    LegalPersonIdentityTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<LegalPersonIdentityTypeEnum> get(String type) {
        for (LegalPersonIdentityTypeEnum typeEnum : LegalPersonIdentityTypeEnum.values()) {
            if (Objects.equals(typeEnum.type, type)) {
                return Optional.of(typeEnum);
            }
        }
        return Optional.empty();
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}