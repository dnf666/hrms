package com.facishare.crm.erpstock.predefine.action;

import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;

/**
 * @author liangk
 * @date 11/05/2018
 */
public class ErpWarehouseBulkDeleteAction extends StandardBulkDeleteAction {

    @Override
    public void before(Arg arg) {
        throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "ERP仓库不允许删除");
    }
}
