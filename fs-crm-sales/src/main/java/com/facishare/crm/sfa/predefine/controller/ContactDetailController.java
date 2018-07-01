package com.facishare.crm.sfa.predefine.controller;


import com.google.common.collect.Lists;

import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.api.DBRecord;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by quzf  合作伙伴联系人业务类型的特殊过滤
 */
@Slf4j
public class ContactDetailController extends ContactDetailBaseController {

    @Override
    protected Result after(Arg arg, Result result) {
        Result newResult = super.after(arg, result);
        ILayout layout = new Layout(newResult.getLayout());

        //1、如果是合作伙伴业务类型，布局中隐藏客户字段、外部来源、外部负责人字段,同时相关对象列表中移除预制的关联联系人的页签
        //2、如果不是合作伙伴业务类型，布局中隐藏合作伙伴字段
        if (PartnerConstants.RECORD_TYPE_CONTACT_PARTNER.equals(this.data.getRecordType())) {
            removeLayoutDetailInfoField(layout, Lists.newArrayList(PartnerConstants.FIELD_CONTACT_ACCOUNT, PartnerConstants.FIELD_OUT_RESOURCES, DBRecord.OUT_OWNER));
            doSupportPartnerRelateTab(layout);
        } else {
            removeLayoutDetailInfoField(layout, Lists.newArrayList(PartnerConstants.FIELD_CONTACT_PARTNER_ID, DBRecord.OUT_OWNER));
        }
        return newResult;
    }


    protected void doSupportPartnerRelateTab(ILayout layout) {
        LayoutExt.of(layout).getRelatedComponent().ifPresent(relatedComponent -> {
            try {
                List<IComponent> childComponents = relatedComponent.getChildComponents();
                childComponents.removeIf(childComponent -> {
                    String relatedListName = childComponent.get("related_list_name", String.class);
                    if (StringUtils.isNotBlank(relatedListName) && !relatedListName.endsWith("__c")) {
                        return true;
                    }
                    return false;
                });
                relatedComponent.setChildComponents(childComponents);
            } catch (MetadataServiceException e) {
                log.warn("ContactDetailController getChildComponents error", e);
            }
        });
    }

}
