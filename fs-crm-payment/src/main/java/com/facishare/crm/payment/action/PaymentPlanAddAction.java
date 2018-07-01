package com.facishare.crm.payment.action;

import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.constant.PaymentPlanObj.PlanPaymentStatus;
import com.facishare.crm.payment.service.PaymentPlanService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class PaymentPlanAddAction extends StandardAddAction {

  private PaymentPlanService paymentPlanService =
          SpringUtil.getContext().getBean(PaymentPlanService.class);

  @Override
  protected void before(Arg arg) {
    log.debug("PaymentPlanAddAction before arg:{}", arg);
    BigDecimal planPaymentAmount =
            new BigDecimal(arg.getObjectData().get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT).toString());
    if (planPaymentAmount.doubleValue() <= 0) {
      throw new ValidateException("计划回款金额应大于0.");
    }
    ObjectDataDocument dataDocument = arg.getObjectData();
    dataDocument.put(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, PlanPaymentStatus.INCOMPLETE.getName());
    dataDocument.put(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, "0");
    super.before(arg);
  }

  @Override
  protected Result after(Arg arg, Result result) {
    result = super.after(arg, result);
    Long planPaymentTime =
            Long.valueOf(objectData.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME).toString());
    String dataId = objectData.get(PaymentPlanObj.ID).toString();
    ParallelUtils.ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> {
      try {
        paymentPlanService.updatePaymentPlanStatus(Lists.newArrayList(objectData), actionContext.getUser());
        paymentPlanService.createOverdueTask(actionContext.getTenantId(), dataId, planPaymentTime);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    });
    try {
      task.run();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }

    return result;
  }
}
