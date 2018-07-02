package com.facishare.crm.electronicsign.enums.status;

import java.util.Objects;
import java.util.Optional;

public enum SingleSignStatusEnum {
    //签署失败 算 未签署，还是可以继续进行重新签署的
    UN_SIGN("1", "未签署"),
    SIGNED("2", "已签署");

    private String status;
    private String message;

    SingleSignStatusEnum(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<SingleSignStatusEnum> get(String status) {
        for (SingleSignStatusEnum statusEnum : SingleSignStatusEnum.values()) {
            if (Objects.equals(statusEnum.status, status)) {
                return Optional.of(statusEnum);
            }
        }
        return Optional.empty();
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}