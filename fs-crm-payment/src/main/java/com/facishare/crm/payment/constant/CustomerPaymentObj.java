package com.facishare.crm.payment.constant;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Set;


public class CustomerPaymentObj {

  public static final String FIELD_ID = "_id";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_ACCOUNT_ID = "account_id";
  public static final String FIELD_ORDER_ID = "order_id";
  public static final String FIELD_PAYMENT_AMOUNT = "payment_amount";
  public static final String FIELD_PAYMENT_TIME = "payment_time";
  public static final String FIELD_PAYMENT_METHOD = "payment_term";
//  public static final String FIELD_PAYMENT_STATUS = "payment_status";
  public static final String FIELD_APPROVE_EMPLOYEE_ID = "finance_employee_id";
  public static final String FIELD_APPROVE_TIME = "finance_confirm_time";
  public static final String FIELD_REMIND_TIME = "notification_time";
  public static final String FIELD_OWNER = "owner";
  public static final String FIELD_OWNER_DEPARTMENT = "owner_department";
  public static final String FIELD_RECORD_TYPE = "record_type";
  public static final String FIELD_ATTACHMENT = "attachment";
  public static final String FIELD_REMARK = "remark";
  public static final String FIELD_LIFE_STATUS = "life_status";

  public static final String PAYMENT_METHOD_DNR = "10002";
  public static final String PAYMENT_METHOD_DNR_LABEL = "预存款+返利";
  public static final String PAYMENT_METHOD_DEPOSIT = "10000";
  public static final String PAYMENT_METHOD_DEPOSIT_LABEL = "预存款";
  public static final String PAYMENT_METHOD_REBATE = "10001";
  public static final String PAYMENT_METHOD_REBATE_LABEL = "返利";

  public static final String ORDER_PAYMENT_LIST = "order_payment_list";

  public static final String FIELD_PAYMENT_STATUS_CONFIRMED = "confirmed";


  public enum messageType {
    CREATE("create", "创建"),
    CONFIRM("confirm", "确认"),
    OVERDUE("reject", "驳回");

    private String name;
    private String value;

    messageType(String value, String name) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }


  @Data
  public static class PaymentMessage {
    @JSONField(name = "objectIds")
    private Set< String > objectIds;
    @JSONField(name = "tenantId")
    private String tenantId;
    @JSONField(name = "messageType")
    private String messageType;
  }

}
