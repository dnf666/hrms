package com.facishare.crm.payment.initialize;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.initialize.CrmPackageSelectOneFieldDescribe.SelectOneOption;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.LinkedList;

public class PaymentPlanObjectDescribe extends CrmPackageObjectDescribe {

  private static final String NAME = "回款计划";
  private static final String TABLE_NAME = "payment_plan";
  private static final String DESCRIPTION = "回款计划";

  private static BaseFieldDescribe name = new CrmSystemAutoNumberFieldDescribe(
      PaymentPlanObj.FIELD_NAME,
      "回款计划编号", true, true, "{yyyy}{mm}{dd}-", "", 1, 6);
  private static CrmPackageFieldDescribe accountId = new CrmPackageReferenceFieldDescribe(
      PaymentPlanObj.FIELD_ACCOUNT_ID, "客户名称", true, false, "AccountObj",
      "related_list_a_payment_plan", "回款计划",
      null, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  @SuppressWarnings("unchecked")
  private static CrmPackageFieldDescribe orderId = new CrmPackageReferenceFieldDescribe(
      PaymentPlanObj.FIELD_ORDER_ID, "销售订单编号", true, false, "SalesOrderObj",
      "related_list_o_payment_plan", "回款计划",
      Lists.newArrayList(
          ImmutableMap.of(
              "connector", "OR",
              "filters", Lists.newArrayList(
                  ImmutableMap.of(
                      "field_name", PaymentPlanObj.FIELD_ACCOUNT_ID,
                      "operator", "EQ",
                      "value_type", 2,
                      "field_values",
                      Lists.newArrayList("$" + PaymentPlanObj.FIELD_ACCOUNT_ID + "$")
                  )
              )
          )), PaymentPlanObj.FIELD_ACCOUNT_ID, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required"))
  );
  private static CrmPackageFieldDescribe orderAmount = new CrmPackageQuoteFieldDescribe(
      PaymentPlanObj.FIELD_ORDER_AMOUNT, "销售订单金额", false, true,
      PaymentPlanObj.FIELD_ORDER_ID + "__r.order_amount", "currency", BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe planPaymentAmount = new CrmPackageCurrencyFieldDescribe(
      PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT, "计划回款金额（元）", true, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe planPaymentTime = new CrmPackageDateFieldDescribe(
      PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME, "计划回款日期", true, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe planPaymentMethod = new CrmPackageSelectOneFieldDescribe(
      PaymentPlanObj.FIELD_PLAN_PAYMENT_METHOD, "计划回款方式", false, false, Lists.newArrayList(
      new SelectOneOption("支票", "1", false),
      new SelectOneOption("现金", "2", false),
      new SelectOneOption("邮政汇款", "3", false),
      new SelectOneOption("电汇", "4", false),
      new SelectOneOption("网上转账", "5", false),
      new SelectOneOption("支付宝", "6", false),
      new SelectOneOption("微信支付", "7", false),
      new SelectOneOption("在线支付", "9", false),
      new SelectOneOption("线下支付", "10", false),
      new SelectOneOption("预存款", "10000"),
      new SelectOneOption("返利", "10001"),
      new SelectOneOption("预存款+返利", "10002")
  ), null, BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));
  @SuppressWarnings("unchecked")
  private static CrmPackageFieldDescribe actualPaymentAmount = new CrmPackageCountFieldDescribe(
      PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, "实际回款金额（元）", false, true, "sum", PaymentObject.ORDER_PAYMENT.getApiName(), OrderPaymentObj.FIELD_PAYMENT_AMOUNT,
      OrderPaymentObj.FIELD_PAYMENT_PLAN_ID,
      "currency", ImmutableMap.of("decimal_places", "2", "return_type", "number","wheres", Lists.newArrayList(
      ImmutableMap.of("connector", "OR", "filters", Lists.newArrayList(
          ImmutableMap.of("field_name", "life_status", "operator", "EQ", "field_values", Lists.newArrayList("normal"), "value_type", 0)
      )),
      ImmutableMap.of("connector", "OR", "filters", Lists.newArrayList(
          ImmutableMap.of("field_name", "life_status", "operator", "EQ", "field_values", Lists.newArrayList("in_change"), "value_type", 0)
      ))
  )), BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required", "is_readonly")));
  private static CrmPackageFieldDescribe planPaymentStatus = new CrmPackageSelectOneFieldDescribe(
      PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, "状态", false, true, Lists.newArrayList(
      new SelectOneOption("未完成", "incomplete"),
      new SelectOneOption("已完成", "completed"),
      new SelectOneOption("已逾期", "overdue")
  ), null, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe remindTime = new CrmPackageNumberFieldDescribe(
      PaymentPlanObj.FIELD_REMIND_TIME, "提前几日提醒", false, false, BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));

  public PaymentPlanObjectDescribe() {
    super(PaymentObject.PAYMENT_PLAN.getApiName(), NAME, TABLE_NAME, DESCRIPTION);
  }

  @Override
  public LinkedList<BaseFieldDescribe> initializeFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(accountId);
    fields.add(orderId);
    fields.add(orderAmount);
    fields.add(planPaymentAmount);
    fields.add(planPaymentTime);
    fields.add(planPaymentMethod);
    fields.add(actualPaymentAmount);
    fields.add(planPaymentStatus);
    fields.add(remindTime);
    fields.add(owner);
    fields.add(ownerDepartment);
    fields.add(extendObjectDataId);
    fields.add(attachment);
    fields.add(remark);
    fields.add(sysApproveEmployeeId);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeDetailLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(accountId);
    fields.add(orderId);
    fields.add(orderAmount);
    fields.add(planPaymentAmount);
    fields.add(planPaymentTime);
    fields.add(planPaymentMethod);
    fields.add(actualPaymentAmount);
    fields.add(planPaymentStatus);
    fields.add(remindTime);
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
    fields.add(planPaymentMethod);
    fields.add(planPaymentAmount);
    fields.add(planPaymentTime);
    fields.add(planPaymentStatus);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeTopLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(accountId);
    fields.add(orderId);
    fields.add(planPaymentAmount);
    fields.add(planPaymentTime);
    fields.add(planPaymentMethod);
    fields.add(planPaymentStatus);
    fields.add(owner);
    return fields;
  }
}
