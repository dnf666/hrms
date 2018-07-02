package com.facishare.crm.deliverynote.predefine.service;

import com.facishare.crm.deliverynote.predefine.service.dto.DeliveryNoteType;
import com.facishare.crm.deliverynote.predefine.service.dto.DeliveryNoteType.*;
import com.facishare.crm.deliverynote.predefine.service.dto.EmptyResult;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * 发货单
 * Created by chenzs on 2018/1/9.
 */
@ServiceModule("delivery_note")
public interface DeliveryNoteService {
    /**
     * 查询"发货单开关"是否开启
     */
    @ServiceMethod("is_delivery_note_enable")
    DeliveryNoteType.IsDeliveryNoteEnableResult isDeliveryNoteEnable(ServiceContext serviceContext);

    // TODO: 2018/1/31 chenzs 一开始没有授权接口，用于修复数据
    @ServiceMethod("init_privilege")
    DeliveryNoteType.EnableDeliveryNoteResult initPrivilege(ServiceContext serviceContext);

    // TODO: 2018/1/31 chenzs 一开始没有授权接口，用于修复数据
    @ServiceMethod("init_print_template")
    DeliveryNoteType.EnableDeliveryNoteResult initPrintTemplate(ServiceContext serviceContext);

    // TODO: 2018/1/31 chenzs 测试
    @ServiceMethod("add_func_access_for_role")
    DeliveryNoteType.EnableDeliveryNoteResult addFuncAccessForRole(ServiceContext serviceContext);

    /**
     * createupdatecustomerorderdeliverytoreceivedtask "把所有已发货状态的订单改成已收货状态"
     */
    @ServiceMethod("test_update_sales_order_status")
    DeliveryNoteType.EnableDeliveryNoteResult testUpdateSalesOrderStatus(ServiceContext serviceContext);

    /**
     * 启用"发货单"开关
     */
    @ServiceMethod("enable_delivery_note")
    DeliveryNoteType.EnableDeliveryNoteResult enableDeliveryNote(ServiceContext serviceContext);

    // TODO: 2018/1/16 chenzs 提供manager就OK，后面测试完删掉
    /**
     * 启用"发货单"开关, 添加字段
     */
    @ServiceMethod("add_field")
    DeliveryNoteType.AddFieldResult addField(ServiceContext serviceContext);

    /**
     * 【刷库】修改
     *    发货单   的'发货仓库'为非必填，改 describe + layout
     *    发货单产品的'库存'   为非必填，改 describe  (layout原来不展示，所以就不用更新了）
     */
    @ServiceMethod("change_field_require")
    DeliveryNoteType.EmptyResult changeFieldRequire(ServiceContext serviceContext);

    /**
     * 【刷库】6.3，
     *     发货单   添加[发货总金额、收货日期、收货备注]             3个字段, 旧数据的处理（describe、layout、data）
     *     发货单产品添加[平均单价、本次发货金额、本次收货数、收货备注] 4个字段, 旧数据的处理（describe、layout、data）
     */
    @ServiceMethod("add_field_describe_and_data")
    DeliveryNoteType.AddFieldResult addFieldDescribeAndData(ServiceContext serviceContext);

    /**
     * 【刷库】6.3，发货单产品的业务类型，去掉'分配业务类型'、'新建'按钮
     */
    @ServiceMethod("update_delivery_note_product_describe_config")
    DeliveryNoteType.EmptyResult updateDeliveryNoteProductDescribeConfig(ServiceContext serviceContext);

    /**
     * 【刷库】6.3
     *  如果销售订单    的defaultLayout没有'已发货金额'（delivered_amount_sum），则加上
     *  如果销售订单产品的defaultLayout没有'已发货数'、'发货金额小计'（delivered_count,delivery_amount），则加上
     */
    @ServiceMethod("sales_order_default_layout_add_fields")
    DeliveryNoteType.EmptyResult salesOrderDefaultLayoutAddFields(ServiceContext serviceContext);

    /**
     * 查询订单的订货仓库
     */
    @ServiceMethod("get_warehouse_by_sales_order_id")
    GetWarehouseBySalesOrderIdModel.Result getWarehouseBySalesOrderId(ServiceContext serviceContext, GetWarehouseBySalesOrderIdModel.Arg arg);

    /**
     * 查询订单对应仓库的可发货产品
     */
    @ServiceMethod("get_can_deliver_products")
    GetCanDeliverProductsModel.Result getCanDeliverProducts(ServiceContext serviceContext, GetCanDeliverProductsModel.Arg arg);

    /**
     * 确认收货
     */
    @ServiceMethod("confirm_receive")
    EmptyResult confirmReceive(ServiceContext serviceContext, DeliveryNoteType.ConfirmReceiveArg arg);

    /**
     * 获取订单的发货单及已完成发货的发货单产品
     */
    @ServiceMethod("get_by_delivery_note_id")
    DeliveryNoteType.GetByDeliveryNoteIdResult getByDeliveryNoteId(ServiceContext serviceContext, DeliveryNoteType.GetByDeliveryNoteIdArg arg);

    /**
     * 获取订单的发货单及已完成发货的发货单产品
     */
    @ServiceMethod("get_by_sales_order_id")
    DeliveryNoteType.GetBySalesOrderIdResult getBySalesOrderId(ServiceContext serviceContext, DeliveryNoteType.GetBySalesOrderIdArg arg);

    @ServiceMethod("get_logistics")
    GetLogisticsResult getLogistics(ServiceContext serviceContext, DeliveryNoteType.GetLogisticsArg arg);
}