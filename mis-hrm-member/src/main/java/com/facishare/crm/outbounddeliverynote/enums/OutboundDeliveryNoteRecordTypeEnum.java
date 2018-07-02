package com.facishare.crm.outbounddeliverynote.enums;

/**
 * @author linchf
 * @date 2018/3/15
 */
public enum OutboundDeliveryNoteRecordTypeEnum {
    DefaultOutbound("default__c", "预设业务类型"),

    SalesOutbound("sales__c", "销售出库"),

    RequisitionOutbound("requisition__c", "调拨出库");

    public String apiName;
    public String label;

    OutboundDeliveryNoteRecordTypeEnum(String apiName, String label) {
        this.apiName = apiName;
        this.label = label;
    }
}
