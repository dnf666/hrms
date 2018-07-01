package com.facishare.crm.electronicsign.enums.type;

import java.util.Optional;

public enum SwitchTypeEnum {
    TENANT_ELECTRONIC_SIGN(1, "租户电子签章"),
    TENANT_REMAINED_QUOTA_ALARM(2, "租户余额警告");

    private int type;
    private String message;

    SwitchTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Optional<SwitchTypeEnum> get(int type) {
        for (SwitchTypeEnum typeEnum : SwitchTypeEnum.values()) {
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