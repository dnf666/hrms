package com.facishare.crm.deliverynote.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum  SalesOrderLogisticsStatusEnum {
    ToBeShipped(1, "待发货"),
    PartialDelivery(2, "部分发货"),
    Consigned(3, "已发货"),
    PartialReceipt(4, "部分收货"),
    Received (5, "已收货"),
    ;

    private int status;
    private String message;

    SalesOrderLogisticsStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<SalesOrderLogisticsStatusEnum> get(int status) {
        for (SalesOrderLogisticsStatusEnum statusEnum : SalesOrderLogisticsStatusEnum.values()) {
            if (statusEnum.status == status) {
                return Optional.of(statusEnum);
            }
        }
        return Optional.empty();
    }
}
