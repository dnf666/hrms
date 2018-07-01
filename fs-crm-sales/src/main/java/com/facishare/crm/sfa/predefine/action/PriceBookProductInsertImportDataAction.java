package com.facishare.crm.sfa.predefine.action;

import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;

import java.util.List;

/**
 * Created by luxin on 2018/4/2.
 */
public class PriceBookProductInsertImportDataAction extends StandardInsertImportDataAction {

    @Override
    protected void customValidate(List<ImportData> dataList) {
        // do nothing
    }


    @Override
    protected void customDefaultValue(List<IObjectData> validList) {
        validList.forEach(objectData -> objectData.set("record_type", "default__c"));
        super.customDefaultValue(validList);
    }

}
