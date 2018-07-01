package com.facishare.crm.payment.action;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.service.PaymentPlanService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class PaymentPlanEditAction extends StandardEditAction {
  private PaymentPlanService paymentPlanService =
          SpringUtil.getContext().getBean(PaymentPlanService.class);

  @Override
  protected Set<String> getIgnoreFieldsForApproval() {
    Set<String> result = super.getIgnoreFieldsForApproval();
    result.add(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS);
    return result;
  }

  @Override
  protected Result after(Arg arg, Result result) {
    result = super.after(arg, result);

    ParallelUtils.ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> {
      try {
        IObjectData dbObjectData = serviceFacade.findObjectData(actionContext.getUser(),
                objectData.getId(), PaymentObject.PAYMENT_PLAN.getApiName());
        Long planPaymentTime =
                Long.valueOf(dbObjectData.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME).toString());
        paymentPlanService.updatePaymentPlanStatus(Lists.newArrayList(dbObjectData), actionContext.getUser());
        //未完成，生成task
        if (dbObjectData.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS).equals(PaymentPlanObj.PlanPaymentStatus.INCOMPLETE.getName())) {
          paymentPlanService.createOverdueTask(actionContext.getTenantId(), dbObjectData.getId(), planPaymentTime);
        }
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
