package com.facishare.crm.electronicsign.enums;

import java.util.Optional;

public enum UserObjEnum {
    CRM_ACCOUNT(1, "CRM客户"),
    TENANT(2, "租户")
    ;

    private int type;
    private String message;

    UserObjEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<UserObjEnum> get(int type) {
        for (UserObjEnum typeEnum : UserObjEnum.values()) {
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