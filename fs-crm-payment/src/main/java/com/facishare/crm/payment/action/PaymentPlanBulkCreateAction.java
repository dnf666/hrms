package com.facishare.crm.payment.action;

import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.constant.PaymentPlanObj.PlanPaymentStatus;
import com.facishare.crm.payment.service.PaymentPlanService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardBulkCreateAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class PaymentPlanBulkCreateAction extends StandardBulkCreateAction {

  private PaymentPlanService paymentPlanService =
          SpringUtil.getContext().getBean(PaymentPlanService.class);

  @Override
  protected void before(Arg arg) {
    log.debug("PaymentPlanBulkCreateAction before arg:{}", arg);
    List< ObjectDataDocument > list = arg.getDataList();
    Double total = 0D;
    String orderId = null;
    for (ObjectDataDocument dataDocument : list) {
      BigDecimal planPaymentAmount =
              new BigDecimal(dataDocument.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT).toString());
      if(planPaymentAmount.doubleValue()<=0){
        throw new ValidateException("计划回款金额应大于0.");
      }
      dataDocument
          .put(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, PlanPaymentStatus.INCOMPLETE.getName());
      dataDocument.put(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, "0");
      orderId = (String) dataDocument.get(PaymentPlanObj.FIELD_ORDER_ID);
      if (orderId == null) {
        throw new ValidateException("订单不能为空");
      }
      Object amount = dataDocument.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT);
      if (amount != null) {
        total += Double.valueOf(amount.toString());
      }
    }
    IObjectData order = serviceFacade
        .findObjectData(actionContext.getUser(), orderId, CrmPackageObjectConstants.ORDER_API_NAME);
    if (order != null && order.get(CrmPackageObjectConstants.ORDER_FIELD_ORDER_AMOUNT) != null) {
      Double orderAmount =
          Double.valueOf(order.get(CrmPackageObjectConstants.ORDER_FIELD_ORDER_AMOUNT).toString());
      if (total > orderAmount) {
        throw new ValidateException("计划回款金额不能大于订单金额");
      }
    }
    super.before(arg);

  }

  @Override
  protected Result after(Arg arg, Result result) {
    result = super.after(arg, result);

    ParallelUtils.ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> {
      try {
        paymentPlanService.updatePaymentPlanStatus(objectDataList, actionContext.getUser());
        for (IObjectData objectData : objectDataList) {
          Long planPaymentTime =
                  Long.valueOf(objectData.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME).toString());
          String dataId = objectData.get(PaymentPlanObj.ID).toString();
          paymentPlanService.createOverdueTask(actionContext.getTenantId(), dataId, planPaymentTime);
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
