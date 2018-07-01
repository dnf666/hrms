package com.facishare.crm.enums;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

/**
 * 发货单对象的状态
 * Created by chenzs on 2018/1/9.
 */
public enum DeliveryNoteObjStatusEnum {
    UN_DELIVERY("un_delivery", "未发货"),
    IN_APPROVAL("in_approval", "审核中"),
    HAS_DELIVERED("has_delivered", "已发货"),
    CHANGING("changing", "变更中"),
    INVALID("invalid", "已作废"),
    RECEIVED("received", "已收货");

    private final String status;
    private final String label;

    DeliveryNoteObjStatusEnum(String status, String label) {
        this.status = status;
        this.label = label;
    }

    public static DeliveryNoteObjStatusEnum getByCode(String status) {
        for (DeliveryNoteObjStatusEnum srcType : values()) {
            if (Objects.equals(status, srcType.status)) {
                return srcType;
            }
        }
        throw new IllegalArgumentException("status error");
    }

    public String getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public static List<String> getAllStatus() {
        List<String> allStatus = Lists.newArrayList();
        for (DeliveryNoteObjStatusEnum d : DeliveryNoteObjStatusEnum.values()) {
            allStatus.add(d.getStatus());
        }
        return allStatus;
    }
}