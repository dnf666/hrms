package com.facishare.crm.sfa.utilities.constant;

import com.google.common.collect.Sets;

import com.facishare.crm.openapi.Utils;

import java.util.Set;

public interface PartnerConstants {
    Set<String> SUPPORT_PARTNER_APINAME = Sets.newHashSet(Utils.ACCOUNT_API_NAME, Utils.SALES_ORDER_API_NAME, Utils.OPPORTUNITY_API_NAME, Utils.LEADS_API_NAME);
    String FIELD_PARTNER_ID = "partner_id";
    String FIELD_OUT_RESOURCES = "out_resources";
    //合作伙伴联系人业务类型
    String RECORD_TYPE_CONTACT_PARTNER = "default_contact_partner__c";
    //联系人下客户字段
    String FIELD_CONTACT_ACCOUNT = "account_id";
    //联系人下所属合作伙伴
    String FIELD_CONTACT_PARTNER_ID = "owned_partner_id";

}
