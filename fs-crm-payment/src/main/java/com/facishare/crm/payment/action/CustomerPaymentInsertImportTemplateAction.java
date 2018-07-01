package com.facishare.crm.payment.action;

import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportTemplateAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.google.common.collect.Lists;

import java.util.List;

public class CustomerPaymentInsertImportTemplateAction extends
        StandardInsertImportTemplateAction {

    private List<String> REMOVE_FIELDS = Lists.newArrayList(
            CustomerPaymentObj.FIELD_ORDER_ID,
            CustomerPaymentObj.FIELD_PAYMENT_AMOUNT,
            "extend_obj_data_id",
            "submit_time",
            "approve_employee_id"
    );

    @Override
    protected void customHeader(List<IFieldDescribe> headerFieldList) {
        headerFieldList.removeIf(f -> REMOVE_FIELDS.contains(f.getApiName()));
    }
}
