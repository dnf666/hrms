package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.controller.CustomerPaymentOpenPayController.Arg;
import com.facishare.crm.payment.controller.CustomerPaymentOpenPayController.Result;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.model.PreDefineController;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import java.util.List;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.Builder;
import lombok.Data;

public class CustomerPaymentOpenPayController extends PreDefineController<Arg, Result> {

  private CustomerPaymentService service = SpringUtil.getContext()
      .getBean(CustomerPaymentService.class);

  @Override
  protected List<String> getFuncPrivilegeCodes() {
    return StandardController.List.getFuncPrivilegeCodes();
  }

  @Override
  protected Result doService(Arg arg) {
    return Result.builder().openPayUrl(
        service.generateOpenPayQrCodeUrl(controllerContext.getUser(), arg.getPaymentId())).build();
  }

  @Data
  public static class Arg {
    private String paymentId;
  }

  @Data
  @Builder
  public static class Result {
    private String openPayUrl;
  }
}
