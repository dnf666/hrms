package com.facishare.crm.payment.action;

import com.facishare.paas.appframework.core.predef.action.StandardLockAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerPaymentLockAction extends StandardLockAction {

  @Override
  protected Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    log.info("CustomerPaymentLock arg:{}", arg.toString());
    return result;
  }
}

