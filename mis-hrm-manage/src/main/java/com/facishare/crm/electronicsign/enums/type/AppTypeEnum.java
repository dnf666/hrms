package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum AppTypeEnum {
    DING_HUO_TONG("1", "订货通"),   //自定义对象那边，SelectOption的value是String
    ACCOUNT_STATEMENT("2", "对账单"),
    ;

    private String type;
    private String label;

    AppTypeEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Optional<AppTypeEnum> get(String type) {
        for (AppTypeEnum typeEnum : AppTypeEnum.values()) {
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