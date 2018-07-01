package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.google.common.collect.Lists;

import java.util.List;

public class QuoteLinesUpdateImportVerifyAction extends StandardUpdateImportVerifyAction {
    private List<String> REMOVE_FIELDS = Lists.newArrayList(
            "extend_obj_data_id"
    );

    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fields = super.getValidImportFields();
        if(QuoteValidator.enablePriceBook(objectDescribe)){
            REMOVE_FIELDS.add(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName());
        }
        fields.removeIf(f -> REMOVE_FIELDS.contains(f.getApiName()));
        return fields;
    }
}
