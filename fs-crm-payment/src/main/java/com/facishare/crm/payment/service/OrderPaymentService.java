package com.facishare.crm.payment.service;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.service.SfaOrderPaymentService;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.dto.Args;
import com.facishare.crm.payment.utils.FieldUtils;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.*;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.ActionContext;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.facishare.crm.payment.constant.CustomerPaymentObj.FIELD_PAYMENT_STATUS_CONFIRMED;
import static com.facishare.crm.payment.constant.CustomerPaymentObj.PAYMENT_METHOD_DNR;

@ServiceModule("order_payment")
@Component
@Slf4j
public class OrderPaymentService {
  @Autowired
  private ServiceFacade serviceFacade;
  private SfaOrderPaymentService orderPaymentService = SpringUtil.getContext().getBean(SfaOrderPaymentService.class);

  public BigDecimal calculateActualPaymentAmount(User user,String planId){
    BigDecimal result = BigDecimal.ZERO;
    if(StringUtils.isBlank(planId)){
      return result;
    }
    List<IObjectData> dataList = Lists.newArrayList();
    SearchTemplateQuery query = new SearchTemplateQuery();
    Filter filter = new Filter();
    filter.setFieldValues(Lists.newArrayList(planId));
    filter.setOperator(Operator.EQ);
    filter.setFieldName(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID);
    query.addFilters(Lists.newArrayList(filter));
    query.setLimit(100);
    query.setOffset(0);
    query.setPermissionType(0);
    while (true) {
      QueryResult< IObjectData > queryResult =
          serviceFacade.findBySearchQuery(user, PaymentObject.ORDER_PAYMENT.getApiName(), query);
      List< IObjectData > list = queryResult.getData();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      dataList.addAll(list);
      query.setOffset(query.getOffset() + query.getLimit());
    }
    for (IObjectData data:dataList ){
      Object o = data.get(OrderPaymentObj.FIELD_PAYMENT_AMOUNT);
      Object life = data.get(CrmPackageObjectConstants.FIELD_LIFE_STATUS);
      if (o != null && life != null && life.toString().equals(SystemConstants.LifeStatus.Normal.value)){
        result = result.add(BigDecimal.valueOf(Double.valueOf(o.toString())));
      }
    }
    return result;
  }
  @POST
  @ServiceMethod("calculate_orders_payment_money")
  public Map<String,BigDecimal> calculateOrdersPaymentMoney(ServiceContext context,Args.CalculateOrdersPaymentMoneyArg arg){
    log.debug("calculateOrdersPaymentMoney arg: {} context: {}", JsonUtil.toJson(arg), context);
    Map<String,BigDecimal> map = Maps.newHashMap();
    if(CollectionUtils.isEmpty(arg.getIds())|| StringUtils.isBlank(arg.getStatus())){
      return map;
    }
    arg.getIds().forEach(x-> map.put(x,BigDecimal.ZERO));
    SearchTemplateQuery query = new SearchTemplateQuery();
    query.addFilters(
        Lists.newArrayList(
            FieldUtils.buildFilter(OrderPaymentObj.FIELD_ORDER_ID,arg.getIds(),Operator.IN, 0),
            FieldUtils.buildFilter(OrderPaymentObj.FIELD_LIFE_STATUS,Lists.newArrayList(arg.getStatus()),Operator.EQ,0))
    );
    if(arg.isDnr()){
      query.getFilters().add(
          FieldUtils.buildFilter(OrderPaymentObj.FIELD_PAYMENT_METHOD,Lists.newArrayList(
              CustomerPaymentObj.PAYMENT_METHOD_DNR,
              CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
              CustomerPaymentObj.PAYMENT_METHOD_REBATE
          ),Operator.IN, 0)
      );
    }
    query.setLimit(100);
    query.setOffset(0);
    query.setPermissionType(0);

    IActionContext newContext = new ActionContext();
    newContext.setDoCalculate(false);
    newContext.setEnterpriseId(context.getTenantId());
    newContext.setUserId(context.getUser().getUserId());

    while (true) {
      QueryResult< IObjectData > queryResult = serviceFacade.findBySearchQuery(newContext, PaymentObject.ORDER_PAYMENT.getApiName(), query);
      List< IObjectData > list = queryResult.getData();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      list.forEach(x->{
        Object o = x.get(CustomerPaymentObj.FIELD_PAYMENT_AMOUNT);
        if(o == null){
          return;
        }
        BigDecimal amount = BigDecimal.valueOf(Double.valueOf(o.toString()));
        String orderId = x.get(CustomerPaymentObj.FIELD_ORDER_ID, String.class);
        map.put(orderId,map.getOrDefault(orderId,BigDecimal.ZERO).add(amount));
      });
      query.setOffset(query.getOffset() + query.getLimit());
    }
    return map;
  }

  private SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdResult getOrderPaymentCostByOrderPaymentId(
      String orderPaymentId, ControllerContext context) {
    SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdArg arg =
        new SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdArg();
    arg.setOrderPaymentId(orderPaymentId);
    ServiceContext serviceContext = new ServiceContext(context.getRequestContext(), null, null);
    return orderPaymentService.getOrderPaymentCostByOrderPaymentId(arg, serviceContext);
  }

  public List< ObjectDataDocument > parseOrderPaymentCost(List< ObjectDataDocument > list, ControllerContext context){
    for (ObjectDataDocument dataDocument : list) {
      String method = (String) dataDocument.get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
      String orderPaymentId = (String) dataDocument.get(CustomerPaymentObj.FIELD_ID);
      if (StringUtils.isNotBlank(method) && StringUtils.isNotBlank(orderPaymentId)
          && CustomerPaymentObj.PAYMENT_METHOD_DNR_LABEL.equals(method)) {
        SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdResult cost = null;
        try {
          cost = this.getOrderPaymentCostByOrderPaymentId(orderPaymentId, context);
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
        }
        if (cost != null){
          dataDocument.put(OrderPaymentObj.PREPAY, cost.getPrepayAmount());
          dataDocument.put(OrderPaymentObj.REBATE_OUTCOME, cost.getRebateOutcomeAmount());
        }
      }
    }
    return list;
  }
}
