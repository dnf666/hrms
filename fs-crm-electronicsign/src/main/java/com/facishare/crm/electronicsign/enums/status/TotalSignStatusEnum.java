package com.facishare.crm.electronicsign.enums.status;

import java.util.Objects;
import java.util.Optional;

public enum TotalSignStatusEnum {
    //签署失败 算 未签署，还是可以继续进行重新签署的
    UN_SIGN("1", "都未签署"),
    ALL_SIGNED("2", "都已签署"),
    PART_SIGNED("3", "部分签署"),
    NO_CREATE_CONTRACT("4", "未创建合同");

    private String status;
    private String message;

    TotalSignStatusEnum(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<TotalSignStatusEnum> get(String status) {
        for (TotalSignStatusEnum statusEnum : TotalSignStatusEnum.values()) {
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