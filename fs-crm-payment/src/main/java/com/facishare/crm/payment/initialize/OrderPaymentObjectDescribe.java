package com.facishare.crm.payment.initialize;

import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INEFFECTIVE;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INVALID;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_IN_CHANGE;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_NORMAL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_UNDER_REVIEW;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.initialize.CrmPackageSelectOneFieldDescribe.SelectOneOption;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.LinkedList;

public class OrderPaymentObjectDescribe extends CrmPackageObjectDescribe {

  private static final String NAME = "回款明细";
  private static final String TABLE_NAME = "payment_order";
  private static final String DESCRIPTION = "回款明细";

  private static BaseFieldDescribe name = new CrmSystemAutoNumberFieldDescribe(
      OrderPaymentObj.FIELD_NAME, "回款明细编号", true, true, "{yyyy}{mm}{dd}-", "", 1, 6);
  private static CrmPackageFieldDescribe paymentId = new CrmPackageMasterFieldDescribe(
      OrderPaymentObj.FIELD_PAYMENT_ID, "回款编号", true, false, PaymentObject.CUSTOMER_PAYMENT.getApiName(),
      "detail_list_order_payment", "回款明细",
      true, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe accountId = new CrmPackageReferenceFieldDescribe(
      PaymentPlanObj.FIELD_ACCOUNT_ID, "客户名称", false, true, "AccountObj",
      "related_list_a_o_payment", "回款明细", null, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe orderId = new CrmPackageReferenceFieldDescribe(
      OrderPaymentObj.FIELD_ORDER_ID, "销售订单编号", true, false, "SalesOrderObj", "related_list_o_op",
      "回款明细", null, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  @SuppressWarnings("unchecked")
  private static CrmPackageFieldDescribe paymentPlanId = new CrmPackageReferenceFieldDescribe(
      OrderPaymentObj.FIELD_PAYMENT_PLAN_ID, "回款计划编号", false, false, PaymentObject.PAYMENT_PLAN.getApiName(),
      "related_list_pl_op", "回款明细",
      Lists.newArrayList(
          ImmutableMap.of(
              "connector", "OR",
              "filters", Lists.newArrayList(
                  ImmutableMap.of(
                      "field_name", OrderPaymentObj.FIELD_ORDER_ID,
                      "operator", "EQ",
                      "value_type", 2,
                      "field_values",
                      Lists.newArrayList("$" + OrderPaymentObj.FIELD_ORDER_ID + "$")
                  )
              )
          )), BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe paymentAmount = new CrmPackageCurrencyFieldDescribe(
      OrderPaymentObj.FIELD_PAYMENT_AMOUNT, "本次回款金额（元）", true, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe paymentMethod = new CrmPackageQuoteFieldDescribe(
      OrderPaymentObj.FIELD_PAYMENT_METHOD, "回款方式", false, true,
      OrderPaymentObj.FIELD_PAYMENT_ID + "__r." + CustomerPaymentObj.FIELD_PAYMENT_METHOD,
      "select_one", BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe paymentTime = new CrmPackageQuoteFieldDescribe(
      OrderPaymentObj.FIELD_PAYMENT_TIME, "回款日期", false, true,
      OrderPaymentObj.FIELD_PAYMENT_ID + "__r." + CustomerPaymentObj.FIELD_PAYMENT_TIME,
      "date_time", BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe approveEmployeeId = new CrmPackageQuoteFieldDescribe(
      OrderPaymentObj.FIELD_APPROVE_EMPLOYEE_ID, "财务确认人", false, true,
      OrderPaymentObj.FIELD_PAYMENT_ID + "__r." + CustomerPaymentObj.FIELD_APPROVE_EMPLOYEE_ID,
      "employee", BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe approveTime = new CrmPackageQuoteFieldDescribe(
      OrderPaymentObj.FIELD_APPROVE_TIME, "财务确认时间", false, true,
      OrderPaymentObj.FIELD_PAYMENT_ID + "__r." + CustomerPaymentObj.FIELD_APPROVE_TIME,
      "date_time", BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe remindTime = new CrmPackageQuoteFieldDescribe(
      OrderPaymentObj.FIELD_REMIND_TIME, "提醒日期", false, true,
      OrderPaymentObj.FIELD_PAYMENT_ID + "__r." + CustomerPaymentObj.FIELD_REMIND_TIME,
      "date_time", BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe lifeStatus = new CrmPackageSelectOneFieldDescribe(
      "life_status", "状态", true, false, Lists.newArrayList(
      new SelectOneOption("未生效", LIFE_STATUS_VALUE_INEFFECTIVE),
      new SelectOneOption("审核中", LIFE_STATUS_VALUE_UNDER_REVIEW),
      new SelectOneOption("已回款", LIFE_STATUS_VALUE_NORMAL),
      new SelectOneOption("变更中", LIFE_STATUS_VALUE_IN_CHANGE),
      new SelectOneOption("已作废", LIFE_STATUS_VALUE_INVALID)
  ), LIFE_STATUS_VALUE_INEFFECTIVE);
  private static CrmPackageFieldDescribe submitTime = new CrmPackageQuoteFieldDescribe("submit_time", "审批提交时间", false, true,
      OrderPaymentObj.FIELD_PAYMENT_ID + "__r.submit_time",
      "date_time", BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));

  public OrderPaymentObjectDescribe() {
    super(PaymentObject.ORDER_PAYMENT.getApiName(), NAME, TABLE_NAME, DESCRIPTION);
  }

  @Override
  public LinkedList<BaseFieldDescribe> initializeFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(paymentId);
    fields.add(accountId);
    fields.add(orderId);
    fields.add(paymentPlanId);
    fields.add(paymentAmount);
    fields.add(paymentTime);
    fields.add(paymentMethod);
    fields.add(approveEmployeeId);
    fields.add(approveTime);
    fields.add(remindTime);
    fields.add(owner);
    fields.add(ownerDepartment);
    fields.add(extendObjectDataId);
    fields.add(attachment);
    fields.add(remark);
    fields.add(lifeStatus);
    fields.add(submitTime);
    fields.add(sysApproveEmployeeId);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeDetailLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(accountId);
    fields.add(paymentId);
    fields.add(orderId);
    fields.add(paymentPlanId);
    fields.add(paymentAmount);
    fields.add(owner);
    fields.add(ownerDepartment);
    fields.add(recordType);
    fields.add(attachment);
    fields.add(remark);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeListLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(paymentTime);
    fields.add(paymentAmount);
    fields.add(paymentMethod);
    fields.add(lifeStatus);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeTopLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(accountId);
    fields.add(orderId);
    fields.add(paymentAmount);
    fields.add(owner);
    fields.add(ownerDepartment);
    return fields;
  }
}
