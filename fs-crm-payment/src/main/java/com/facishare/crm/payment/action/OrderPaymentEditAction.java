package com.facishare.crm.payment.action;

import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.google.common.collect.Lists;

import java.util.List;

public class OrderPaymentEditAction extends StandardEditAction{
  @Override
  protected List< String > getFuncPrivilegeCodes() {
    return Lists.newArrayList();
  }

  @Override
  protected void before(Arg arg) {
   throw new ValidateException("回款明细不能单独修改");
  }
}
