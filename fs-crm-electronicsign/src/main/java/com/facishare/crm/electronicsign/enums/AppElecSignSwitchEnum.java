package com.facishare.crm.electronicsign.enums;

import java.util.Objects;
import java.util.Optional;

/**
 * 业务级电子签章开关状态
 */
public enum AppElecSignSwitchEnum {
    ON(1, "开启"),
    OFF(2, "关闭");

    private Integer status;
    private String message;

    AppElecSignSwitchEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<AppElecSignSwitchEnum> get(Integer status) {
        for (AppElecSignSwitchEnum switchEnum : AppElecSignSwitchEnum.values()) {
            if (Objects.equals(switchEnum.status, status)) {
                return Optional.of(switchEnum);
            }
        }
        return Optional.empty();
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}