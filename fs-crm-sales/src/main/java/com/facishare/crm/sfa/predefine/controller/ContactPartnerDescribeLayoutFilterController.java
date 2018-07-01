package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Sets;

import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.paas.metadata.api.DBRecord;

import java.util.Objects;

/**
 * @author quzf  联系人---合作伙伴员工的特殊处理类
 * @date 2018/3/22 11:06
 */
public class ContactPartnerDescribeLayoutFilterController extends SFADescribeLayoutController {
    @Override
    protected void handleLayout(Arg arg, Result result) {
        super.handleLayout(arg, result);
        if (arg.getLayout_type() == null || Objects.isNull(formComponent)) {
            return;
        }
        //1、如果是合作伙伴联系人业务类型数据，则移除预制的查找关联客户字段、外部来源、外部负责人字段
        //2、如果不是合作伙伴业务类型数据，则移除预制的查找关联合作伙伴字段
        if (PartnerConstants.RECORD_TYPE_CONTACT_PARTNER.equals(arg.getRecordType_apiName())) {
            PreDefLayoutUtil.removeSomeFields(formComponent, Sets.newHashSet(PartnerConstants.FIELD_CONTACT_ACCOUNT, PartnerConstants.FIELD_OUT_RESOURCES, DBRecord.OUT_OWNER));
        } else {
            PreDefLayoutUtil.removeSomeFields(formComponent, Sets.newHashSet(PartnerConstants.FIELD_CONTACT_PARTNER_ID, DBRecord.OUT_OWNER));
        }
    }


}
