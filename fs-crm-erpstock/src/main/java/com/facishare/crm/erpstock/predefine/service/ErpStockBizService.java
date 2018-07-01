package com.facishare.crm.erpstock.predefine.service;

import com.facishare.crm.erpstock.predefine.service.dto.ErpStockType;
import com.facishare.crm.erpstock.predefine.service.model.IsErpStockEnableModel;
import com.facishare.crm.erpstock.predefine.service.model.QueryErpStockConfigModel;
import com.facishare.crm.erpstock.predefine.service.model.SaveErpStockConfigModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;

/**
 * @author linchf
 * @date 2018/5/14
 */
@ServiceModule("erp_stock_biz")
public interface ErpStockBizService {
    /**
     * 开启Erp库存
     */
    @ServiceMethod("enable_erp_stock")
    ErpStockType.EnableErpStockResult enableErpStock(ServiceContext serviceContext) throws MetadataServiceException;

    /**
     * 关闭Erp库存
     * @param serviceContext
     * @return
     */
    @ServiceMethod("close_erp_stock")
    ErpStockType.CloseErpStockResult closeErpStock(ServiceContext serviceContext);

    /**
     * 保存库存设置
     */
    @ServiceMethod("save_erp_stock_config")
    SaveErpStockConfigModel.Result saveErpStockConfig(ServiceContext serviceContext, SaveErpStockConfigModel.Arg arg);

    /**
     * 查询库存配置
     *
     * @param serviceContext
     * @return
     */
    @ServiceMethod("query_erp_stock_config")
    QueryErpStockConfigModel.Result queryErpStockConfig(ServiceContext serviceContext);

    /**
     * ERP库存是否开启
     * @param serviceContext
     * @return
     */
    @ServiceMethod("is_erp_stock_enable")
    IsErpStockEnableModel.Result isErpStockEnable(ServiceContext serviceContext);
}
