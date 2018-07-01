package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportTemplateAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by linchf on 2018/2/5.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseInsertImportTemplateAction extends StandardInsertImportTemplateAction {
    @Override
    protected void customHeader(List<IFieldDescribe> headerFieldList) {
        StockUtils.getWarehouseTemplateHeader(headerFieldList);
    }
}
