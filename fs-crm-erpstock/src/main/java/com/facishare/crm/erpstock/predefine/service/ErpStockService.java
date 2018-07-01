package com.facishare.crm.erpstock.predefine.service;

import com.facishare.crm.erpstock.predefine.service.model.ErpStockInvalidOrQueryModel;
import com.facishare.crm.erpstock.predefine.service.model.IsErpStockEnableModel;
import com.facishare.crm.erpstock.predefine.service.model.base.BaseInvalidModel;
import com.facishare.crm.erpstock.predefine.service.model.base.BaseQueryModel;
import com.facishare.crm.erpstock.predefine.service.model.base.BaseSaveModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * openApi调用 只支持内部调用
 * @author linchf
 * @date 2018/5/8
 */
@ServiceModule("ErpStockObj")
public interface ErpStockService {

    /**
     * 保存
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("save")
    BaseSaveModel.Result save(ServiceContext serviceContext, BaseSaveModel.Arg arg);

    /**
     * 更新
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("update")
    BaseSaveModel.Result update(ServiceContext serviceContext, BaseSaveModel.Arg arg);

    /**
     * 作废
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("invalid")
    BaseInvalidModel.Result invalid(ServiceContext serviceContext, ErpStockInvalidOrQueryModel.Arg arg);

    /**
     * 查询
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("query")
    BaseInvalidModel.Result query(ServiceContext serviceContext, ErpStockInvalidOrQueryModel.Arg arg);

    /**
     * 是否开启ERP库存 对接使用
     * @param serviceContext
     * @return
     */
    @ServiceMethod("is_erp_stock_enable")
    IsErpStockEnableModel.Result isErpStockEnable(ServiceContext serviceContext);
}
