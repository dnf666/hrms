package com.facishare.crm.stock.predefine.service;

import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;


/**
 * @author liangk
 * @date 23/03/2018
 */
@ServiceModule("product")
public interface ProductService {
    /**
     * 根据产品id列表，查询产品详情，提供给前端
     */
    @ServiceMethod("query_product_by_ids")
    StockType.QueryProductByIdsResult queryProductByIds(ServiceContext serviceContext, StockType.QueryProductByIdsArg arg);
}
