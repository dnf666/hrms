package com.facishare.crm.payment.action;

import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class CustomerPaymentBulkRecoverAction extends StandardBulkRecoverAction {

  private CustomerPaymentService customerPaymentService =
      SpringUtil.getContext().getBean(CustomerPaymentService.class);

  @Override
  @Transactional
  public Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    log.debug("CustomerPaymentBulkRecoverAction doAct arg:{} result: {}", arg, result);
    customerPaymentService.bulkRecoverSyncAccountInfo(actionContext, arg, result);
    if (result.getSuccess()) {
      for( String id : arg.getIdList()) {
        IObjectData data = serviceFacade.findObjectData(actionContext.getUser(), id, objectDescribe);
        customerPaymentService.updateOrderPayment(data, actionContext.getUser());
      }
    }
    return result;
  }

}

