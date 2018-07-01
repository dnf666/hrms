package com.facishare.crm.payment.constant;

public class PaymentPlanObj {

  public static final String FIELD_NAME = "name";
  public static final String FIELD_ACCOUNT_ID = "account_id";
  public static final String FIELD_ORDER_ID = "order_id";
  public static final String FIELD_ORDER_AMOUNT = "order_amount";
  public static final String FIELD_PLAN_PAYMENT_AMOUNT = "plan_payment_amount";
  public static final String FIELD_PLAN_PAYMENT_TIME = "plan_payment_time";
  public static final String FIELD_PLAN_PAYMENT_METHOD = "plan_payment_method";
  public static final String FIELD_PLAN_PAYMENT_STATUS = "plan_payment_status";
  public static final String FIELD_ACTUAL_PAYMENT_AMOUNT = "actual_payment_amount";
  public static final String FIELD_REMIND_TIME = "remind_time";
  public static final String FIELD_OWNER = "owner";
  public static final String FIELD_OWNER_DEPARTMENT = "owner_department";
  public static final String FIELD_RECORD_TYPE = "record_type";
  public static final String FIELD_ATTACHMENT = "attachment";
  public static final String FIELD_REMARK = "remark";
  public static final String FIELD_LIFE_STATUS = "life_status";

  public static final String EXTEND_OBJ_DATA_ID = "extend_obj_data_id";
  public static final String ID = "_id";

  public enum PlanPaymentStatus {
    INCOMPLETE("incomplete", "未完成"),

    COMPLETED("completed", "已完成"),

    OVERDUE("overdue", "已逾期");

    private String name;
    private String value;

    PlanPaymentStatus(String name, String value) {
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
}
