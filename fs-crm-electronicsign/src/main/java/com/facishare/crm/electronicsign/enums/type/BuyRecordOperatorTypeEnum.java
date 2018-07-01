package com.facishare.crm.electronicsign.enums.type;

import java.util.Objects;
import java.util.Optional;

public enum BuyRecordOperatorTypeEnum {
    FS_ADMIN(1, "纷享管理员"),
    TENANT_EMPLOYEE(2, "租户员工");

    private Integer type;
    private String label;

    BuyRecordOperatorTypeEnum(Integer type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Optional<BuyRecordOperatorTypeEnum> get(Integer type) {
        for (BuyRecordOperatorTypeEnum typeEnum : BuyRecordOperatorTypeEnum.values()) {
            if (Objects.equals(typeEnum.type, type)) {
                return Optional.of(typeEnum);
            }
        }
        return Optional.empty();
    }

    public Integer getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

}