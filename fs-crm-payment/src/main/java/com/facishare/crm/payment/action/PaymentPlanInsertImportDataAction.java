package com.facishare.crm.payment.action;

import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;

import java.util.List;

public class PaymentPlanInsertImportDataAction extends StandardInsertImportDataAction {

  @Override
  protected void customDefaultValue(List< IObjectData > validList) {
    super.customDefaultValue(validList);
    validList.forEach(data -> {
      data.set(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS,
          PaymentPlanObj.PlanPaymentStatus.INCOMPLETE.getName());
      data.set(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, "0");
    });
  }
}
