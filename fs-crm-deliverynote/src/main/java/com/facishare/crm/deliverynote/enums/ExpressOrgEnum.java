package com.facishare.crm.deliverynote.enums;

import java.util.Objects;

/**
 * 物流公司
 * Created by chenzs on 2018/1/10.
 */
public enum ExpressOrgEnum {
    SF("SF", "顺丰速运"),
    HTKY("HTKY", "百世快递"),
    ZTO("ZTO", "中通快递"),
    STO("STO", "申通快递"),
    YTO("YTO", "圆通速递"),
    YD("YD", "韵达速递"),
    YZPY("YZPY", "邮政快递包裹"),
    EMS("EMS", "EMS"),
    HHTT("HHTT", "天天快递"),
    JD("JD", "京东物流"),
    QFKD("QFKD", "全峰快递"),
    GTO("GTO", "国通快递"),
    UC("UC", "优速快递"),
    DBL("DBL", "德邦"),
    FAST("FAST", "快捷快递"),
    ZJS("ZJS", "宅急送"),
    AJ("AJ", "安捷快递"),
    AMAZON("AMAZON", "亚马逊物流"),
    UPS("UPS", "UPS"),
    FEDEX("FEDEX", "FEDEX联邦(国内件）"),
    FEDEX_GJ("FEDEX_GJ", "FEDEX联邦(国际件）"),
    OTHER("other", "其他");    //SelectOne SelectMany如果有"其他"选项,要用小写的"other"，不然编辑会有两个"其他"，王凡他们那边出现了

    private final String code;
    private final String label;

    ExpressOrgEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ExpressOrgEnum getByCode(String code) {
        for (ExpressOrgEnum srcType : values()) {
            if (Objects.equals(code, srcType.code)) {
                return srcType;
            }
        }
        throw new IllegalArgumentException("code error");
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}