package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum UserIdentityTypeEnum {
    Identification_card("0", "身份证");

    private String type;
    private String message;

    UserIdentityTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<UserIdentityTypeEnum> get(int type) {
        for (UserIdentityTypeEnum typeEnum : UserIdentityTypeEnum.values()) {
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