package com.facishare.crm.electronicsign.enums.status;

import java.util.Optional;

/**
 * 电子签章初始化状态
 */
public enum ElecSignInitStatusEnum {
    NOT_OPEN(0, "未开启"),
    OPEN_FAIL(1, "开启失败"),
    OPENED(2, "开启成功");

    private int status;
    private String message;

    ElecSignInitStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<ElecSignInitStatusEnum> get(int status) {
        for (ElecSignInitStatusEnum statusEnum : ElecSignInitStatusEnum.values()) {
            if (statusEnum.status == status) {
                return Optional.of(statusEnum);
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