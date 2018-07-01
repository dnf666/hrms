package com.facishare.crm.electronicsign.enums;

import java.util.Optional;

/**
 * 租户级电子签章开关状态
 */
public enum TenantElecSignSwitchEnum {
    ON(1, "开启"),
    OFF(2, "关闭");

    private int status;
    private String message;

    TenantElecSignSwitchEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<TenantElecSignSwitchEnum> get(int status) {
        for (TenantElecSignSwitchEnum switchEnum : TenantElecSignSwitchEnum.values()) {
            if (switchEnum.status == status) {
                return Optional.of(switchEnum);
            }
        }
        return Optional.empty();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}