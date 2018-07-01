package com.facishare.crm.electronicsign.enums.status;

import java.util.Objects;
import java.util.Optional;

public enum BestSignCertifyStatusEnum {
    New_apply("1", "新申请"),
    Applying("2", "已认证"),
    Time_out("3", "超时"),
    Apply_fail("4", "申请失败"),
    Success("5", "成功");

    private String status;
    private String message;

    BestSignCertifyStatusEnum(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<BestSignCertifyStatusEnum> get(String status) {
        for (BestSignCertifyStatusEnum statusEnum : BestSignCertifyStatusEnum.values()) {
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