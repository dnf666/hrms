package com.facishare.crm.stock.predefine.action;

import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by linchf on 2018/2/5.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseInsertImportDataAction extends StandardInsertImportDataAction {
    @Override
    protected void customDefaultValue(List<IObjectData> validList) {
        super.customDefaultValue(validList);
        validList.forEach(objectData -> {
            objectData.set("account_range", "{\"type\":\"noCondition\",\"value\":\"ALL\"}");
            objectData.set("record_type", "default__c");
        });
    }
}
