package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.metadata.util.SpringUtil;


public class CustomerPaymentFullDetailController extends CustomerPaymentDetailController {

  private CustomerPaymentService customerPaymentService = SpringUtil.getContext()
      .getBean(CustomerPaymentService.class);

  @Override
  protected void before(Arg arg) {
    arg.setFromRecycleBin(true);
    super.before(arg);
  }

  @Override
  protected Result doService(Arg arg) {
    Result r = super.doService(arg);
    if (null != r && null != r.getData()) {
      r.setData(customerPaymentService.fillWithDetails(controllerContext.getRequestContext(),
          controllerContext.getObjectApiName(), r.getData()));
    }
    return r;
  }
}
