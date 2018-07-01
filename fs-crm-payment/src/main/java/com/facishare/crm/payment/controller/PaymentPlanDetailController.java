package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.service.OrderPaymentService;
import com.facishare.crm.payment.utils.FieldUtils;
import com.facishare.crm.payment.utils.JsonObjectUtils;
import com.facishare.crm.payment.utils.JsonPaths;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import static com.facishare.crm.payment.utils.PaymentPlanUtils.getPaymentPlanStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PaymentPlanDetailController extends StandardDetailController {

  @Override
  public Result doService(Arg arg) {
    Result result = super.doService(arg);

    //layout 回款计划状态 实际回款金额
    String paymentStatusPath =
        JsonPaths.DESCRIBE_LAYOUT_DETAIL_FORM_BASE_FIELDS + "[?(@.field_name=='"
            + PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS + "')]";
    if (JsonObjectUtils.get(result, Map.class, paymentStatusPath) == null) {
      result = JsonObjectUtils.append(result, PaymentPlanDetailController.Result.class,
          JsonPaths.DESCRIBE_LAYOUT_DETAIL_FORM_BASE_FIELDS, FieldUtils
              .buildLayoutField(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, false, false,
                  "select_one"));
    }

    String realPaymentAmountPath =
        JsonPaths.DESCRIBE_LAYOUT_DETAIL_FORM_BASE_FIELDS + "[?(@.field_name=='"
            + PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT + "')]";
    if (JsonObjectUtils.get(result, Map.class, realPaymentAmountPath) == null) {
      result = JsonObjectUtils.append(result, PaymentPlanDetailController.Result.class,
          JsonPaths.DESCRIBE_LAYOUT_DETAIL_FORM_BASE_FIELDS, FieldUtils
              .buildLayoutField(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, false, true, "number"));
    }

    //value 回款计划状态 实际回款金额
    ObjectDataDocument dataDocument = result.getData();
    BigDecimal actualPaymentAmount =
        new BigDecimal(dataDocument.get(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT).toString());
    BigDecimal planPaymentAmount =
        new BigDecimal(dataDocument.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT).toString());
    Long planPaymentTime =
        Long.valueOf(dataDocument.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME).toString());
    String paymentPlanStatus =
        getPaymentPlanStatus(planPaymentAmount, actualPaymentAmount, planPaymentTime);
    dataDocument.put(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, paymentPlanStatus);
    dataDocument.put(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, actualPaymentAmount);
    result.setData(dataDocument);

    //layout top
//    List< Map< String, Object > > topLayout = Lists.newArrayList();
//    topLayout.add(FieldUtils
//        .buildLayoutField(PaymentPlanObj.FIELD_ACCOUNT_ID, false, true, "object_reference"));
//    topLayout.add(FieldUtils
//        .buildLayoutField(PaymentPlanObj.FIELD_ORDER_ID, false, true, "object_reference"));
//    topLayout.add(FieldUtils
//        .buildLayoutField(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT, false, true, "currency"));
//    topLayout.add(
//        FieldUtils.buildLayoutField(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME, false, true, "date"));
//    topLayout.add(FieldUtils
//        .buildLayoutField(PaymentPlanObj.FIELD_PLAN_PAYMENT_METHOD, false, false, "select_one"));
//    topLayout.add(FieldUtils
//        .buildLayoutField(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, false, true, "select_one"));
//    topLayout.add(FieldUtils.buildLayoutField(PaymentPlanObj.FIELD_OWNER, false, true, "employee"));
//    result = JsonObjectUtils
//        .update(result, Result.class, JsonPaths.DESCRIBE_LAYOUT_DETAIL_TOP_FIELDS, topLayout);

    return result;
  }

}
