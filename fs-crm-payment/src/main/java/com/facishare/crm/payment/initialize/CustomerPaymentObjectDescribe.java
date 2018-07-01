package com.facishare.crm.payment.initialize;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.initialize.CrmPackageSelectOneFieldDescribe.SelectOneOption;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INEFFECTIVE;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INVALID;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_IN_CHANGE;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_NORMAL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_UNDER_REVIEW;

public class CustomerPaymentObjectDescribe extends CrmPackageObjectDescribe {

  private static final String NAME = "回款";
  private static final String TABLE_NAME = "payment_customer";
  private static final String DESCRIPTION = "回款";

  private static BaseFieldDescribe name = new CrmSystemAutoNumberFieldDescribe(
      CustomerPaymentObj.FIELD_NAME, "回款编号", true, true, "{yyyy}{mm}{dd}-", "", 1, 6);
  private static CrmPackageFieldDescribe accountId = new CrmPackageReferenceFieldDescribe(
      CustomerPaymentObj.FIELD_ACCOUNT_ID, "客户名称", true, false, "AccountObj",
      "related_list_a_payment", "回款", null, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe orderId = new CrmPackageLongTextFieldDescribe(
      CustomerPaymentObj.FIELD_ORDER_ID, "订单编号", false, true, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required", "max_length", "default_value")));
  @SuppressWarnings("unchecked")
  private static CrmPackageFieldDescribe paymentAmount = new CrmPackageCountFieldDescribe(
      CustomerPaymentObj.FIELD_PAYMENT_AMOUNT, "本次回款总金额（元）", false, true, "sum", PaymentObject.ORDER_PAYMENT.getApiName(),
      OrderPaymentObj.FIELD_PAYMENT_AMOUNT, OrderPaymentObj.FIELD_PAYMENT_ID, "currency", ImmutableMap.of("decimal_places", "2", "return_type", "number"), BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe paymentTime = new CrmPackageDateFieldDescribe(
      CustomerPaymentObj.FIELD_PAYMENT_TIME, "回款日期", true, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), Lists.newArrayList("is_required")));
  private static CrmPackageFieldDescribe paymentMethod = new CrmPackageSelectOneFieldDescribe(
      CustomerPaymentObj.FIELD_PAYMENT_METHOD, "回款方式", false, false, Lists.newArrayList(
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
  private static CrmPackageFieldDescribe approveEmployeeId = new CrmPackageEmployeeFieldDescribe(
      CustomerPaymentObj.FIELD_APPROVE_EMPLOYEE_ID, "财务确认人", false, false, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe approveTime = new CrmPackageDateTimeFieldDescribe(
      CustomerPaymentObj.FIELD_APPROVE_TIME, "财务确认时间", false, false, BaseFieldDescribe.generateConfig(true, false, false, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe remindTime = new CrmPackageDateFieldDescribe(
      CustomerPaymentObj.FIELD_REMIND_TIME, "提醒日期", false, false, BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));
  private static CrmPackageFieldDescribe lifeStatus = new CrmPackageSelectOneFieldDescribe(
      "life_status", "状态", true, false, Lists.newArrayList(
      new SelectOneOption("未生效", LIFE_STATUS_VALUE_INEFFECTIVE),
      new SelectOneOption("审核中", LIFE_STATUS_VALUE_UNDER_REVIEW),
      new SelectOneOption("已回款", LIFE_STATUS_VALUE_NORMAL),
      new SelectOneOption("变更中", LIFE_STATUS_VALUE_IN_CHANGE),
      new SelectOneOption("已作废", LIFE_STATUS_VALUE_INVALID)
  ), LIFE_STATUS_VALUE_INEFFECTIVE);
  private static CrmPackageFieldDescribe submitTime = new CrmPackageDateTimeFieldDescribe("submit_time", "回款提交时间", false, true);

  public static Map<String, String> ACTION_MAPPING;

  static {
    ACTION_MAPPING = new HashMap<>();
    ACTION_MAPPING.put("6007", "Add");
    ACTION_MAPPING.put("7001", "Confirm");
    ACTION_MAPPING.put("7002", "Edit");
    ACTION_MAPPING.put("7003", "Submit");
    ACTION_MAPPING.put("7004", "Delete");
    ACTION_MAPPING.put("7005", "View");
    ACTION_MAPPING.put("7006", "AddEvent");
    ACTION_MAPPING.put("7007", "Abolish");
    ACTION_MAPPING.put("7008", "Import");
    ACTION_MAPPING.put("7009", "Export");
    ACTION_MAPPING.put("7010", "List");
    ACTION_MAPPING.put("7011", "ChangeOwner");
    ACTION_MAPPING.put("7012", "Recover");
    ACTION_MAPPING.put("7013", "EditTeamMember");
    ACTION_MAPPING.put("7014", "Print");
    ACTION_MAPPING.put("7015", "Lock");
    ACTION_MAPPING.put("7016", "Unlock");
  }

  public CustomerPaymentObjectDescribe() {
    super(PaymentObject.CUSTOMER_PAYMENT.getApiName(), NAME, TABLE_NAME, DESCRIPTION);
  }

  @Override
  public LinkedList<BaseFieldDescribe> initializeFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(accountId);
    fields.add(orderId);
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
    fields.add(paymentTime);
    fields.add(paymentMethod);
    fields.add(remindTime);
    fields.add(approveEmployeeId);
    fields.add(approveTime);
    fields.add(paymentAmount);
    fields.add(owner);
    fields.add(ownerDepartment);
    fields.add(recordType);
    fields.add(attachment);
    fields.add(remark);
    fields.add(lifeStatus);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeListLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(name);
    fields.add(accountId);
    fields.add(paymentTime);
    fields.add(lifeStatus);
    fields.add(paymentAmount);
    return fields;
  }

  @Override
  LinkedList<BaseFieldDescribe> initializeTopLayoutFields() {
    LinkedList<BaseFieldDescribe> fields = Lists.newLinkedList();
    fields.add(accountId);
    fields.add(orderId);
    fields.add(paymentAmount);
    fields.add(paymentTime);
    fields.add(lifeStatus);
    fields.add(owner);
    fields.add(ownerDepartment);
    return fields;
  }
}
