package com.facishare.crm.payment.action;

import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
public class CustomerPaymentInvalidAction extends StandardInvalidAction {

  private CustomerPaymentService customerPaymentService =
      SpringUtil.getContext().getBean(CustomerPaymentService.class);

  private IObjectData customerPayment;

  @Override
  protected void before(Arg arg) {
    log.info("Invaliding CustomerPayment: {}", arg);
    super.before(arg);
    if (CollectionUtils.isNotEmpty(objectDataList)) {
      customerPayment = objectDataList.get(0);
      log.debug("Invaliding CustomerPayment: {}", customerPayment);
    }
  }

  @Override
  public Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    if ( CollectionUtils.isNotEmpty(objectDataList) ) {
      for (IObjectData data : objectDataList) {
        customerPaymentService.updateOrderPayment(data, actionContext.getUser());
      }
    }
    customerPaymentService.invalidCustomerAccount(actionContext.getRequestContext(),
        Lists.newArrayList(customerPayment));
    return result;
  }
}

