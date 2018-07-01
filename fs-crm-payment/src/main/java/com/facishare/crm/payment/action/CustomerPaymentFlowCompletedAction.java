package com.facishare.crm.payment.action;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;

import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import java.util.List;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class CustomerPaymentFlowCompletedAction extends StandardFlowCompletedAction {
  private CustomerPaymentService customerPaymentService =
          SpringUtil.getContext().getBean(CustomerPaymentService.class);

  private List<IObjectData> orderPaymentObjects;

  @Override
  protected void before(Arg arg) {
    log.info("CustomerPaymentFlowCompletedAction before arg: {}", arg);
    super.before(arg);
    if (arg.getTriggerType() == ApprovalFlowTriggerType.INVALID.getTriggerTypeCode()) {
      IObjectDescribe orderPaymentDescribe =
              serviceFacade.findObject(arg.getTenantId(), PaymentObject.ORDER_PAYMENT.getApiName());
      IObjectData masterObject = serviceFacade.findObjectData(arg.getUser(), arg.getDataId(),
              PaymentObject.CUSTOMER_PAYMENT.getApiName());
      orderPaymentObjects =
              serviceFacade.findDetailObjectDataList(orderPaymentDescribe, masterObject, arg.getUser());
    }
  }

  @Override
  @Transactional
  protected Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    log.info("CustomerPaymentFlowCompletedAction doAct arg: {}, result: {}", arg, result);
    return result;
  }

  @Override
  protected Result after(Arg arg, Result result) {
    result = super.after(arg, result);

    if (arg.getTriggerType() == ApprovalFlowTriggerType.CREATE.getTriggerTypeCode() && result
            .getSuccess()) {
      if (arg.getStatus().equals("reject") || arg.getStatus().equals("pass")) {
        customerPaymentService.sendDHTMq(actionContext.getUser(), arg.getStatus(), arg.getDataId());
      }
    }

    ParallelUtils.ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> {
      IObjectData data = serviceFacade.findObjectDataIncludeDeleted(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
      customerPaymentService
              .updateOrderPayment(data, actionContext.getUser());
      customerPaymentService.deletePaymentByEditPayment(actionContext, data);
      customerPaymentService.flowCompletedSyncAccountInfo(actionContext, arg, orderPaymentObjects);
    });
    try {
      task.run();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }

    return result;
  }
}

