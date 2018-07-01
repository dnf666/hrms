package com.facishare.crm.electronicsign.enums;

import java.util.Objects;
import java.util.Optional;

public enum OriginEnum {
    SALES_ORDER("1", "销售订单"),    //自定义对象那边，SelectOption的value是String
    ACCOUNT_STATEMENT("2", "对账单"), //金新农的对账单用了自建的自定义对象，所以这里key不用objApiName
    DELIVERY_NOTE("3", "发货单"),
    ;
    // TODO: 2018/5/22 chenzs 加字段要调整

    private String type;
    private String label;

    OriginEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public static Optional<OriginEnum> get(String type) {
        for (OriginEnum typeEnum : OriginEnum.values()) {
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