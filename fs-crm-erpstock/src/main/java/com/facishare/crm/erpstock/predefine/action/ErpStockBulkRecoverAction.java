package com.facishare.crm.erpstock.predefine.action;

import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linchf
 * @date 2018/5/11
 */
@Slf4j(topic = "erpStockAccess")
public class ErpStockBulkRecoverAction extends StandardBulkRecoverAction {
    @Override
    protected void before(Arg arg) {
        throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "ERP库存不可恢复");
    }
}
