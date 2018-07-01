package com.facishare.crm.payment.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.common.util.ParallelUtils.ParallelTask;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Slf4j
public class CustomerPaymentAddAction extends StandardAddAction {

  private CustomerPaymentService customerPaymentService =
          SpringUtil.getContext().getBean(CustomerPaymentService.class);

  private Map<String, List<JSONObject>> details;

  @Override
  protected void before(Arg arg) {
    validateDuplicateDetails(arg);
    log.debug("CustomerPaymentAddAction before arg:{}", arg);
    arg = customerPaymentService.modifyArg(actionContext, arg);
    arg.getObjectData().put("submit_time", System.currentTimeMillis());
    details = (Map<String, List<JSONObject>>) JSON.parse(JSON.toJSONString(arg.getDetails()));
    super.before(arg);
  }

  private void validateDuplicateDetails(Arg arg) {
    if (null == arg) {
      throw new ValidateException("参数异常.");
    }
    if (null == arg.getDetails() || arg.getDetails().isEmpty()) {
      throw new ValidateException("回款明细不能为空.");
    }
    List<ObjectDataDocument> details =
            arg.getDetails().get(PaymentObject.ORDER_PAYMENT.getApiName());
    if (CollectionUtils.isEmpty(details)) {
      throw new ValidateException("回款明细不能为空.");
    }
    List<String> keys = new ArrayList<>();
    details.forEach(d -> {
      BigDecimal paymentAmount =
              new BigDecimal(d.get(OrderPaymentObj.FIELD_PAYMENT_AMOUNT).toString());
      if (paymentAmount.doubleValue() <= 0) {
        throw new ValidateException("回款明细的本次回款金额应大于0.");
      }
      String orderId = String
              .valueOf(d.getOrDefault(OrderPaymentObj.FIELD_ORDER_ID, OrderPaymentObj.FIELD_ORDER_ID));
      String planId = String.valueOf(d.getOrDefault(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID,
              OrderPaymentObj.FIELD_PAYMENT_PLAN_ID));
      planId = planId == "null" ? "" : planId;
      String key = orderId + planId;
      if (keys.contains(key)) {
        throw new ValidateException("订单编号+回款计划编号不能完全相同.");
      }
      keys.add(key);
    });
  }

  @Override
  protected void modifyDetailObjectDataBeforeCreate(IObjectData masterObjectData, Map<String, List<IObjectData>> objectDataDetail) {
    super.modifyDetailObjectDataBeforeCreate(masterObjectData, objectDataDetail);
    if (objectDataDetail.size() == 0) {
      return;
    }
    objectDataDetail.forEach((apiName, objectDataList) -> {
      objectDataList.stream().forEach(detail -> {
        ObjectDataExt.of(detail).setLifeStatus(ObjectLifeStatus.INEFFECTIVE);
      });
    });
  }

  @Override
  protected Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    log.debug("CustomerPaymentAddAction doAct arg:{} result: {}", arg, result);
    return result;
  }

  @Override
  @Transactional
  protected Result after(Arg arg, Result result) {
    result = super.after(arg, result);

    if (!startApprovalFlowResult.containsKey(objectData.getId())
            || startApprovalFlowResult.get(objectData.getId()) == ApprovalFlowStartResult.APPROVAL_NOT_EXIST) {
      customerPaymentService.sendDHTMq(actionContext.getUser(), "no_flow", objectData.getId());
    }

    ParallelTask task = ParallelUtils.createParallelTask();
    task.submit(() -> {
      log.debug("CustomerPaymentAddAction after arg:{} data: {}", arg, objectData);
      String method = (String) objectData.get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
      customerPaymentService.updateOrderPayment(objectData, actionContext.getUser());
      ArrayList<String> options = Lists.newArrayList(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
              CustomerPaymentObj.PAYMENT_METHOD_REBATE, CustomerPaymentObj.PAYMENT_METHOD_DNR);
      if (StringUtils.isNotBlank(method) && options.contains(method)) {
        customerPaymentService.addSyncAccountInfo(actionContext, objectData, details);
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

