package com.facishare.crm.sfa.predefine.action;

import com.facishare.paas.appframework.core.predef.action.StandardInsertImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;

import java.util.List;

/**
 * Created by luxin on 2018/5/22.
 */
public class PriceBookProductImportVerifyAction extends StandardInsertImportVerifyAction {

    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fieldDescribes = super.getValidImportFields();

        fieldDescribes.removeIf(o -> "extend_obj_data_id".equals(o.getApiName()) || "record_type".equals(o.getApiName()));
        return fieldDescribes;
    }


}
