package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by linchf on 2018/2/5.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseInsertImportVerifyAction extends StandardInsertImportVerifyAction {
    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fieldDescribes = serviceFacade.getTemplateField(actionContext.getUser(), objectDescribe);
        fieldDescribes = StockUtils.getWarehouseTemplateHeader(fieldDescribes);
        return fieldDescribes;
    }
}
