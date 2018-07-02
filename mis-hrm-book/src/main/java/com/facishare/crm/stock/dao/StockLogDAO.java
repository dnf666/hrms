package com.facishare.crm.stock.dao;

import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.predefine.service.model.QueryStockLogByTemplateModel;

import java.util.List;

/**
 * @author linchf
 * @date 2018/3/7
 */
public interface StockLogDAO {
    List<String> bulkSave(List<StockLogDO> stockLogDOs);

    void bulkUpdate(List<StockLogDO> stockLogDOs);

    List<StockLogDO> queryByIds(List<String> ids);

    List<StockLogDO> queryBySalesOrderId(String tenantId, String salesOrderId);

    List<StockLogDO> queryByTemplate(QueryStockLogByTemplateModel.StockLogVO template, int limit, int offset);

    Long countByTemplate(QueryStockLogByTemplateModel.StockLogVO template, int limit, int offset);
}
