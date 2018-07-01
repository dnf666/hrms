package com.facishare.crm.payment.action;

import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.predef.action.StandardFlowStartCallbackAction;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomerPaymentFlowStartCallbackAction extends StandardFlowStartCallbackAction {
  private CustomerPaymentService customerPaymentService =
          SpringUtil.getContext().getBean(CustomerPaymentService.class);

  @Override
  protected Result after(Arg arg, Result result) {
    result = super.after(arg, result);

    ParallelUtils.ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> {
      log.debug("CustomerPaymentFlowStartCallbackAction after arg:{} data: {}", arg, objectData);
      customerPaymentService.updateOrderPayment(objectData, actionContext.getUser());
    });
    try {
      task.run();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }

    return result;
  }
}
