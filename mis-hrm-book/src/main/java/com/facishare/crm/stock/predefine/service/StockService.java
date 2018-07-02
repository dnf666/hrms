package com.facishare.crm.stock.predefine.service;

import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.crm.stock.predefine.service.model.*;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;

import java.util.List;
import java.util.Map;

/**
 * Created by liangk on 2018/1/8.
 */
@ServiceModule("stock")
public interface StockService {

    /**
     * 查询产品列表在仓库的库存
     */
    @ServiceMethod("query_by_products_id")
    QueryByProductsIdModel.Result queryByProductsId(ServiceContext serviceContext, QueryByProductsIdModel.Arg arg);

    /**
     * 开启库存
     */
    @ServiceMethod("enable_stock")
    StockType.EnableStockResult enableStock(ServiceContext serviceContext) throws MetadataServiceException;

    /**
     * 保存库存设置
     */
    @ServiceMethod("save_stock_config")
    SaveStockConfigModel.Result saveStockConfig(ServiceContext serviceContext, SaveStockConfigModel.Arg arg);

    /**
     * 查询库存配置
     *
     * @param serviceContext
     * @return
     */
    @ServiceMethod("query_stock_config")
    QueryStockConfigModel.Result queryStockConfig(ServiceContext serviceContext);

    /**
     * 清除库存功能权限
     *
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("clean_stock_func")
    CleanStockFuncModel.Result cleanStockFunc(ServiceContext serviceContext, CleanStockFuncModel.Arg arg);

    /**
     * 查询销售订单产品的可用库存
     */
    @ServiceMethod("query_product_available_stocks")
    QueryAvailableStocksModel.Result querySalesOrderProductAvailableStock(ServiceContext serviceContext, QueryAvailableStocksModel.Arg arg);

//    /**
//     * 清除库存对象
//     */
//    @ServiceMethod("clean_stock_describe")
//    CleanStockDescribeModel.Result cleanStockDescribe(ServiceContext serviceContext, CleanStockDescribeModel.Arg arg);

    /**
     * 检查库存 筛选待补货的库存
     * @param arg
     * @return
     */
    @ServiceMethod("check_stock_warning")
    CheckStockWarningModel.Result checkStockWarning(CheckStockWarningModel.Arg arg);

    /**
     * 是否展示待补货的库存菜单
     * @param serviceContext
     * @return
     */
    @ServiceMethod("is_show_stock_warning_menu")
    IsShowStockWarningMenuModel.Result isShowStockWarningMenu(ServiceContext serviceContext);

    /**
     * 6.2 临时关闭库存开关
     * @param serviceContext
     * @param arg
     * @return
     */
    @ServiceMethod("close_stock_switch")
    CloseStockSwitchModel.Result closeStockSwitch(ServiceContext serviceContext, CloseStockSwitchModel.Arg arg);

    /**
     * 更新库存、发货单、入库单、出库单和调拨单的数值型字段精度
     * @param serviceContext
     * @return
     */
    @ServiceMethod("update_field")
    AddOrUpdateFieldModel.Result updateField(ServiceContext serviceContext);

    /**
     * 纷享库存是否开启
     * @param serviceContext
     * @return
     */
    @ServiceMethod("is_stock_enable")
    IsStockEnableModel.Result isStockEnable(ServiceContext serviceContext);

    /**
     * 库存开启类型
     * @see com.facishare.crm.stock.enums.StockTypeEnum
     * @param serviceContext
     * @return
     */
    @ServiceMethod("query_enabled_stock_type")
    QueryStockStatusModel.Result queryEnabledStockType(ServiceContext serviceContext);

    @ServiceMethod("modify_stock")
    ModifyStockModel.Result modifyStock(ServiceContext serviceContext, ModifyStockModel.Arg arg);

    @ServiceMethod("query_goods_received_note_describe_field")
    QueryDescribeFieldModel.Result<Map<String, List<String>>> queryGoodsReceivedNoteDescribeField(ServiceContext serviceContext);
}
