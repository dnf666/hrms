package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum PayTypeEnum {
    SYSTEM("SYSTEM", "系统"),
    ;

    private String type;
    private String label;

    PayTypeEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Optional<PayTypeEnum> get(String type) {
        for (PayTypeEnum typeEnum : PayTypeEnum.values()) {
            if (Objects.equals(typeEnum.type, type)) {
                return Optional.of(typeEnum);
            }
        }
        return Optional.empty();
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

}