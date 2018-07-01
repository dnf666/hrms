package com.facishare.crm.enums;

import java.util.Optional;

/**
 * "发货单"开关状态
 * Created by chenzs on 2018/1/10.
 */
public enum DeliveryNoteSwitchEnum {
    NOT_OPEN(0, "未开启"),
    OPEN_FAIL(1, "开启失败"),
    OPENED(2, "开启成功");

    private int status;
    private String message;

    DeliveryNoteSwitchEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Optional<DeliveryNoteSwitchEnum> get(int status) {
        for (DeliveryNoteSwitchEnum switchEnum : DeliveryNoteSwitchEnum.values()) {
            if (switchEnum.status == status) {
                return Optional.of(switchEnum);
            }
        }
        return Optional.empty();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}