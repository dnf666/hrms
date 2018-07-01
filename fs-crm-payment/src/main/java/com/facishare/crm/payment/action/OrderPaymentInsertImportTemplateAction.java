package com.facishare.crm.payment.action;

import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportTemplateAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.google.common.collect.Lists;

import java.util.List;

public class OrderPaymentInsertImportTemplateAction extends
        StandardInsertImportTemplateAction{

    private List<String> REMOVE_FIELDS = Lists.newArrayList(
            "extend_obj_data_id", "approve_employee_id"
    );

    @Override
    protected void customHeader(List<IFieldDescribe> headerFieldList) {
        headerFieldList.removeIf(f -> REMOVE_FIELDS.contains(f.getApiName()));
    }
}
