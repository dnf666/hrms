package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.controller.CustomerPaymentRelatedOrderController.Arg;
import com.facishare.crm.payment.controller.CustomerPaymentRelatedOrderController.Result;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.crm.payment.service.SalesOrderRemoteService;
import com.facishare.paas.appframework.core.model.PreDefineController;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerPaymentRelatedOrderController extends PreDefineController< Arg, Result > {

  private SalesOrderRemoteService salesOrderRemoteService =
      SpringUtil.getContext().getBean(SalesOrderRemoteService.class);
  private CustomerPaymentService customerPaymentService = SpringUtil.getContext()
          .getBean(CustomerPaymentService.class);

  @Override
  protected List< String > getFuncPrivilegeCodes() {
    return StandardController.List.getFuncPrivilegeCodes();
  }

  @Override
  protected Result doService(Arg arg) {
    List<String> orderIds=arg.getOrderIds();
    if(StringUtils.isNotBlank(arg.getPaymentId())){
      orderIds.clear();
      ServiceContext serviceContext = new ServiceContext( controllerContext.getRequestContext(), null,
              null);
      List<Map> orders=customerPaymentService.queryOrderPaymentList(serviceContext,arg.getPaymentId());
      orders.forEach(item->orderIds.add(item.getOrDefault("order_id","").toString()));
    }

    if (CollectionUtils.isEmpty(orderIds)) {
      return Result.builder().dataList(Lists.newArrayList()).build();
    }
    List< Map< String, Object > > result = salesOrderRemoteService
        .findObjectDataByIds(controllerContext, CrmPackageObjectConstants.ORDER_API_NAME,
                orderIds);
    return Result.builder().dataList(result).build();
  }

  @Data
  public static class Arg {
    private List< String > orderIds =new ArrayList<>();
    private String paymentId;
  }

  @Data
  @Builder
  public static class Result {
    private List< Map< String, Object > > dataList;
  }
}
