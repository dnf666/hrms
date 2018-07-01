package com.facishare.crm.sfainterceptor.predefine.service.model;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
public enum LifeStatusEnum {

    INEFFECTIVE("ineffective", "未生效"),

    UNDER_REVIEW("under_review", "审核中"),

    NORMAL("normal", "正常"),

    IN_CHANGE("in_change", "变更中"),

    INVALID("invalid", "已作废");

    public String value;
    public String label;

    LifeStatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
