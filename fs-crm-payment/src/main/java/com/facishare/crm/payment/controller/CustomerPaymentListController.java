package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerPaymentListController extends StandardListController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPaymentListController.class);

  private CustomerPaymentService service = SpringUtil.getContext().getBean(CustomerPaymentService.class);

  @Override
  protected Result doService(Arg arg) {
    LOGGER.debug("Get CustomerPayment data list, arg: {}", arg);
    Result r = super.doService(arg);
    r.setDataList(service.parseOrderNames(controllerContext.getUser(), r.getDataList()));
    return r;
  }
}
