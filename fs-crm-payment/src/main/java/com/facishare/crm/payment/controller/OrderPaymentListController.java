package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.service.OrderPaymentService;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.util.SpringUtil;

import java.util.List;


public class OrderPaymentListController extends StandardListController {

  @Override
  protected Result doService(Arg arg) {
    Result result = super.doService(arg);
    OrderPaymentService orderPaymentService =
        SpringUtil.getContext().getBean(OrderPaymentService.class);
    List< ObjectDataDocument > list =
        orderPaymentService.parseOrderPaymentCost(result.getDataList(), controllerContext);
    result.setDataList(list);
    return result;
  }
}
