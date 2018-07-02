package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum QuotaTypeEnum {
    INDIVIDUAL("1", "个人"),  //  //自定义对象那边，SelectOption的value是String
    ENTERPRISE("2", "企业");

    private String type;
    private String label;

    QuotaTypeEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Optional<QuotaTypeEnum> get(String type) {
        for (QuotaTypeEnum typeEnum : QuotaTypeEnum.values()) {
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