package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum AccountSignCertifyLayoutTypeEnum {
    DEFAULT("default", "默认"),
    ENTERPRISE("enterprise", "企业"),
    INDIVIDUAL("individual", "个人");

    private String type;
    private String label;

    AccountSignCertifyLayoutTypeEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Optional<AccountSignCertifyLayoutTypeEnum> get(int type) {
        for (AccountSignCertifyLayoutTypeEnum typeEnum : AccountSignCertifyLayoutTypeEnum.values()) {
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