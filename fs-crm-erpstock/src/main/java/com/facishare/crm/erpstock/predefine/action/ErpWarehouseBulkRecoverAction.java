package com.facishare.crm.erpstock.predefine.action;

import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;

/**
 * @author liangk
 * @date 11/05/2018
 */
public class ErpWarehouseBulkRecoverAction extends StandardBulkRecoverAction {

    @Override
    public void before(Arg arg) {
        throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "ERP仓库不允许恢复");
    }
}
