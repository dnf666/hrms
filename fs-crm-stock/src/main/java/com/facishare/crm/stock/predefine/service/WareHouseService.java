package com.facishare.crm.stock.predefine.service;

import com.facishare.crm.stock.predefine.service.model.*;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

import java.util.Map;

/**
 * Created by liangk on 2018/1/8.
 */
@ServiceModule("warehouse")
public interface WareHouseService {

    /**
     * 查询下游订货通客户适用的仓库列表和产品库存
     */
    @ServiceMethod("query_down_valid")
    WareHouseDetailModel.Result queryDownValid(ServiceContext serviceContext, WareHouseDetailModel.Arg arg);

    @ServiceMethod("query_down_valid_by_ids")
    QueryDownValidByIdsModel.Result queryDownValidByIds(ServiceContext serviceContext, QueryDownValidByIdsModel.Arg arg);

    /**
     * 查询上游客户适用的仓库列表
     */
    @ServiceMethod("query_up_valid")
    QueryUpValidWarehouseModel.Result queryUpValid(ServiceContext serviceContext, QueryUpValidWarehouseModel.Arg arg);

    /**
     * 查询所有正常生命状态的仓库
     * @param serviceContext
     * @return
     */
    @ServiceMethod("query_list")
    QueryListWarehouseModel.Result queryList(ServiceContext serviceContext);

    @ServiceMethod("modify_warehouse_not_required")
    ModifyWarehouseNotRequiredModel.Result modifySalesOrderWarehouseNotRequired(ServiceContext serviceContext);
}
