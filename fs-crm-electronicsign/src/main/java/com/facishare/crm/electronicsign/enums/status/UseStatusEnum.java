package com.facishare.crm.electronicsign.enums.status;

import java.util.Objects;
import java.util.Optional;

public enum UseStatusEnum {
    UN_USE("1", "未启用"),  //自定义对象那边，SelectOption的value是String
    ON("2", "已启用"),
    OFF("3", "已停用");

    private String status;
    private String label;

    UseStatusEnum(String status, String label) {
        this.status = status;
        this.label = label;
    }

    public static Optional<UseStatusEnum> get(String status) {
        for (UseStatusEnum statusEnum : UseStatusEnum.values()) {
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