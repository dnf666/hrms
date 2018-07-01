package com.facishare.crm.rest;

import java.util.List;
import java.util.Map;

import com.facishare.crm.rest.dto.*;
import com.facishare.rest.proxy.annotation.*;

@RestResource(value = "CRM_SFA", contentType = "application/json")
public interface CrmRestApi {
    /**
     * header中传递值样例如下：headers:{"x-fs-userInfo":"0","x-fs-ei":"53432","Content-Type":"application/json"},fsUserId=0,ei=53432<br>
     * @param arg 客户账户开关 "key":"29","value":"1";  促销开关 "key":"31","value":"1";
     * @param headers
     * @return
     */
    @POST(value = "/crm/common/setconfigvalue", desc = "同步客户账户启用状态给CRM")
    SyncTenantSwitchModel.Result syncTenantSwitch(@Body SyncTenantSwitchModel.Arg arg, @HeaderMap Map<String, String> headers);

    /**
     * 分页查询所有客户<br>
     * @param arg
     * @param headers
     * @return
     */
    @POST(value = "/crm/customer/query", desc = "添加角色与业务类型的关系")
    QueryCustomersByPage.Result queryCustomersByPage(@Body QueryCustomersByPage.Arg arg, @HeaderMap Map<String, String> headers);

    /**
     * header中传递值样例如下：headers:{"x-fs-userInfo":"0","x-fs-ei":"53432","x-fs-partnertype":"1","Content-Type":"application/json"},fsUserId=0,ei=53432<br>
     * @param arg
     * @param headers
     * @return
     */
    @POST(value = "/crm/customerorder/get_used_credit_amount", desc = "获取客户已用信用")
    GetUsedCreditAmount.Result getUsedCreditAmount(@Body GetUsedCreditAmount.Arg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/base/getplainobjectsbyids", desc = "查询销售订单，包括作废的")
    SalesOrderModel.Result listPlainObjectsByIds(@Body SalesOrderModel.Arg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/customerorder/querylistbypromotionids", desc = "根据促销id，查询关联的订单和订单产品") //        List<String> promotionIds;ObjectRelatedPromotionModel.Arg arg
    ObjectRelatedPromotionModel.Result listRelatedObjectsByPromotionIds(@Body List<String> promotionIds, @HeaderMap Map<String, String> headers);

    @GET(value = "/crm/customerorder", desc = "保存销售订单")
    SalesOrderModel.SaveCustomerOrderResult saveCustomerOrder(@QueryParam("incrementalUpdate") boolean incrementalUpdate, @Body SalesOrderModel.SalesOrderVo salesOrderVo, @HeaderMap Map<String, String> headers);

    @GET(value = "/crm/customerorder/{id}", desc = "查询销售订单")
    SalesOrderModel.GetByIdResult getCustomerOrderById(@PathParam("id") String id, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/customerorder/querylistbyids", desc = "批量查询销售订单")
    SalesOrderModel.GetByIdsResult getCustomerOrderByIds(@Body String[] ids, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/returnorder/querylistbyids", desc = "批量查询退货单")
    ReturnOrderModel.GetByIdsResult getReturnOrderByIds(@Body String[] ids, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/customerorder/setlogisticsstatus", desc = "更新订单物流状态") //发货状态
    SalesOrderModel.SetLogisticsStatusResult setLogisticsStatus(@Body SalesOrderModel.SetLogisticsStatusArg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/customerorder/existsdeliveredorders", desc = "查询是否存在已发货状态的订单")
    SalesOrderModel.ExistsDeliveredOrders existsDeliveredOrders(@HeaderMap Map<String, String> headers);

    /**
     * 返回结果只是确认任务有没有创建成功
     */
    @POST(value = "/crm/customerorder/createupdatecustomerorderdeliverytoreceivedtask", desc = "把所有已发货状态的订单改成已收货状态")
    SalesOrderModel.UpdateCustomerOrderDeliveryToReceivedTask createUpdateCustomerOrderDeliveryToReceivedTask(@HeaderMap Map<String, String> headers);

    @POST(value = "/crm/orderproduct/query", desc = "查询销售订单的产品列表")
    SalesOrderModel.QueryOrderProductResult queryOrderProduct(@Body SalesOrderModel.QueryOrderProductArg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/product/querylistbyids", desc = "根据产品id列表，查询产品详情，供内部使用")
    QueryProductByIds.Result queryListByProductIds(@Body String[] productIds, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/common/getplainobjectsbyids", desc = "根据自定义对象Id列表，查询自定义对象详情，供前端使用")
    QueryObjectByIdsModel.Result queryObjectByIds(@Body QueryObjectByIdsModel.Arg arg, @HeaderMap Map<String, String> headers);

    @GET(value = "/crm/returnorder/{id}", desc = "查询退货单")
    ReturnOrderModel.GetByIdResult getReturnOrderById(@PathParam("id") String id, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/returnorder/query", desc = "通过条件查询退货单")
    ReturnOrderModel.QueryReturnOrderByConditionResult queryReturnOrderByCondition(@Body ReturnOrderModel.QueryReturnOrderArg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/returnproduct/query", desc = "查询退货单的产品列表")
    ReturnOrderModel.QueryReturnOrderProductResult queryReturnOrderProduct(@Body ReturnOrderModel.QueryReturnOrderProductArg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/customerorder/existsnotreceivedorinvalidorders", desc = "查询订单是否满足库存开启条件")
    SalesOrderModel.CheckStockEnableResult checkStockEnable(@HeaderMap Map<String, String> headers);

    @POST(value = "/crm/orderproduct/getorderproductsbyorderids", desc = "根据订单ids查询产品")
    SalesOrderModel.QueryOrderProductResult queryOrderProductByOrderIds(@Body String[] orderIds, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/returnproduct/getreturnproductsbyreturnids", desc = "根据退货单ids查询产品")
    ReturnOrderModel.QueryReturnOrderProductResult queryReturnOrderProductByReturnOrderIds(@Body String[] returnOrderIds, @HeaderMap Map<String, String> headers);

    @POST(value = "/customerorder/updatecustomerorderfordeliverynote", desc = "发货单状态变化时同步更新订单")
    UpdateCustomerOrderForDeliveryNoteModel.Result updateCustomerOrderForDeliveryNote(@Body UpdateCustomerOrderForDeliveryNoteModel.Arg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/orderproduct/getpromotionquantity", desc = "查询促销下产品参与促销的数量")
    BatchGetPromotionProductQuantity.Result getPromotionQuantiy(@Body List<BatchGetPromotionProductQuantity.PromotionProductArg> arg, @HeaderMap Map<String, String> headers);

    @POST(value = "/crm/remindRecord", desc = "设置CRM预警")
    SetRemindRecordModel.Result setRemindRecord(@QueryParam("sessionBOCItemKey") String sessionBOCItemKey, @Body SetRemindRecordModel.Item[] items, @HeaderMap Map<String, String> headers);
}
