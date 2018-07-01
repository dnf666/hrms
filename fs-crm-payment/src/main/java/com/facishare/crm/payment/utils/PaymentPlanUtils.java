package com.facishare.crm.payment.utils;

import com.facishare.crm.payment.constant.PaymentPlanObj.PlanPaymentStatus;
import java.math.BigDecimal;

public class PaymentPlanUtils {

  public static String getPaymentPlanStatus(BigDecimal planPaymentAmount,
      BigDecimal realPaymentAmount, Long planPaymentTime) {
    String planPaymentStatus;
    int result = realPaymentAmount.compareTo(planPaymentAmount);
    if (result < 0) {
      planPaymentStatus = System.currentTimeMillis() > planPaymentTime ?
          PlanPaymentStatus.OVERDUE.getName() :
          PlanPaymentStatus.INCOMPLETE.getName();
    } else {
      planPaymentStatus = PlanPaymentStatus.COMPLETED.getName();
    }
    return planPaymentStatus;
  }

}
