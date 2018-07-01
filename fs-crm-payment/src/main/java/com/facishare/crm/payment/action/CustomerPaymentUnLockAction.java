package com.facishare.crm.payment.action;

import com.facishare.paas.appframework.core.predef.action.StandardUnlockAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerPaymentUnLockAction extends StandardUnlockAction{

  @Override
  protected Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    log.info("CustomerPaymentUnLock arg:{}", arg.toString());
    return result;
  }

}

