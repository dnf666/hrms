package com.facishare.crm.payment.action;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.crm.payment.service.PaymentPlanService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.common.util.ParallelUtils.ParallelTask;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class CustomerPaymentEditAction extends StandardEditAction {

  private CustomerPaymentService customerPaymentService =
          SpringUtil.getContext().getBean(CustomerPaymentService.class);

  private PaymentPlanService paymentPlanService =
          SpringUtil.getContext().getBean(PaymentPlanService.class);

  @Override
  protected void before(Arg arg) {
    if (arg.getDetails() != null) {
      List<ObjectDataDocument> details = arg.getDetails().get(PaymentObject.ORDER_PAYMENT.getApiName());
      if (details != null) {
        List<String> keys = new ArrayList<>();
        details.forEach(d -> {
          String orderId = String
                  .valueOf(d.getOrDefault(OrderPaymentObj.FIELD_ORDER_ID, OrderPaymentObj.FIELD_ORDER_ID));
          String planId = String.valueOf(d.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID));
          planId = planId == "null" ? "" : planId;
          if (keys.contains(orderId + planId)) {
            throw new ValidateException("订单编号+回款计划编号不能完全相同.");
          }
          keys.add(orderId + planId);
        });
      }
    }
    log.debug("CustomerPaymentEditAction before arg:{}", arg);
    arg = customerPaymentService.modifyArg(actionContext, arg);
    super.before(arg);
  }

  @Override
  protected Set<String> getIgnoreFieldsForApproval() {
    Set<String> result = super.getIgnoreFieldsForApproval();
    result.add(CustomerPaymentObj.FIELD_ORDER_ID);
    return result;
  }

  @NotNull
  @Override
  protected List<String> modifyObjectDataWhenStartApprovalFlowByResultMap(ApprovalFlowTriggerType type, List<IObjectData> objectDataList, Map<String, ApprovalFlowStartResult> resultMap) {
    List<String> fieldsProjection = super.modifyObjectDataWhenStartApprovalFlowByResultMap(type, objectDataList, resultMap);
    if (fieldsProjection.isEmpty()) {
      return fieldsProjection;
    }
    for (IObjectData objectData : objectDataList) {
      ApprovalFlowStartResult result = resultMap.get(objectData.getId());
      if (result != null && result == ApprovalFlowStartResult.SUCCESS) {
        objectData.set("submit_time", System.currentTimeMillis());
      }
    }
    fieldsProjection.add("submit_time");
    return fieldsProjection;
  }

  @Override
  @Transactional
  protected Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    log.debug("CustomerPaymentEditAction doAct arg:{} data:{}", arg, objectData);
    customerPaymentService.updateOrderPayment(objectData, actionContext.getUser());
    ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> customerPaymentService.editSyncAccountInfo(actionContext, objectData));
    try {
      task.run();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return result;
  }

  @Override
  protected Result after(Arg arg, Result result) {
    log.debug("CustomerPaymentEditAction after arg:{} result:{}", arg, result);
    result = super.after(arg, result);
    String paymentId = (String) result.getObjectData().get(CustomerPaymentObj.FIELD_ID);
    if (StringUtils.isNotBlank(paymentId)) {
      ParallelTask task = ParallelUtils.createParallelTask();
      task.submit(() -> {
        customerPaymentService.deletePaymentByEditPayment(actionContext, objectData);
        if (!detailsToDelete.isEmpty()) {
          Set<String> planIds = Sets.newHashSet();
          detailsToDelete.forEach(x -> {
            if (x.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID) != null) {
              planIds.add(x.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID).toString());
            }
          });
          List<IObjectData> playObjectDataList =
                  serviceFacade.findObjectDataByIds(actionContext.getUser().getTenantId(),
                          new ArrayList<>(planIds), PaymentObject.PAYMENT_PLAN.getApiName());
          paymentPlanService.updatePaymentPlanStatus(playObjectDataList, actionContext.getUser());
        }
      });
      try {
        task.run();
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    }
    return result;
  }
}
