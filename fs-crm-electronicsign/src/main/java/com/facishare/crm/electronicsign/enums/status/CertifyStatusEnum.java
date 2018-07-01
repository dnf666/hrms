package com.facishare.crm.electronicsign.enums.status;

import java.util.Objects;
import java.util.Optional;

public enum CertifyStatusEnum {
    NO_RECORD("1", "未认证"), //自定义对象那边，SelectOption的value是String
    CERTIFYING("2", "认证中"),
    CRTTIFIED("3", "已认证"),
    FAIL("4", "认证错误"),
    TIME_OUT("5", "认证超时");

    private String status;
    private String label;

    CertifyStatusEnum(String status, String label) {
        this.status = status;
        this.label = label;
    }

    public static Optional<CertifyStatusEnum> get(String status) {
        for (CertifyStatusEnum statusEnum : CertifyStatusEnum.values()) {
            if (Objects.equals(statusEnum.status, status)) {
                return Optional.of(statusEnum);
            }
        }
        return Optional.empty();
    }

    public String getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }
}