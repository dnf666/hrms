package com.facishare.crm.stock.predefine.service;

import com.facishare.crm.stock.predefine.service.model.QueryStockLogByTemplateModel;
import com.facishare.crm.stock.predefine.service.model.SaveStockLogModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * @author linchf
 * @date 2018/5/31
 */
@ServiceModule("stock_log")
public interface StockLogService {

    @ServiceMethod("query_by_page")
    QueryStockLogByTemplateModel.Result queryByPage(ServiceContext serviceContext, QueryStockLogByTemplateModel.Arg arg);

    /**
     * 补全订单库存操作日志
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("save_sales_order_stock_log")
    SaveStockLogModel.Result saveSalesOrderStockLog(ServiceContext serviceContext, SaveStockLogModel.Arg arg);
}
