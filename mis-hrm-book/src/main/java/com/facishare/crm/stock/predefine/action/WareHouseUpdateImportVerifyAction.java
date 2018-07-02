package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by linchf on 2018/2/5.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseUpdateImportVerifyAction extends StandardUpdateImportVerifyAction {
    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fieldDescribes = serviceFacade.getUpdateImportTemplateField(actionContext.getUser(), objectDescribe);
        return StockUtils.getWarehouseTemplateHeader(fieldDescribes);
    }
}
