package com.facishare.crm.sfa.predefine.action;

import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportTemplateAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.google.common.collect.Lists;

import java.util.List;

public class QuoteUpdateImportTemplateAction extends StandardUpdateImportTemplateAction {

    private List<String> REMOVE_FIELDS = Lists.newArrayList(
            "extend_obj_data_id",
            "account_id"
    );

    @Override
    protected void customHeader(List<IFieldDescribe> headerFieldList) {
        super.customHeader(headerFieldList);
        headerFieldList.removeIf(f -> REMOVE_FIELDS.contains(f.getApiName()));
    }
}
