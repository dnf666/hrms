package com.facishare.crm.stock.enums;

/**
 * @author liangk
 * @date 21/03/2018
 */
public enum GoodsReceivedNoteRecordTypeEnum {
    DefaultGoodsReceivedNote("default__c", "预设业务类型"),

    RequisitionIn("requisition_in__c", "调拨入库");

    public String apiName;
    public String label;

    GoodsReceivedNoteRecordTypeEnum(String apiName, String label) {
        this.apiName = apiName;
        this.label = label;
    }
}
