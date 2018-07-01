package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum UserTypeEnum {
    INDIVIDUAL("1", "个人"),
    ENTERPRISE("2", "企业");

    private String type;
    private String message;

    UserTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<UserTypeEnum> get(String type) {
        for (UserTypeEnum typeEnum : UserTypeEnum.values()) {
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