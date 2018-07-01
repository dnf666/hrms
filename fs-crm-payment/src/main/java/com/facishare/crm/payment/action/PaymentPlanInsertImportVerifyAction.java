package com.facishare.crm.payment.action;

import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.google.common.collect.Lists;
import java.util.List;

public class PaymentPlanInsertImportVerifyAction extends StandardInsertImportVerifyAction {

  private List<String> REMOVE_FIELDS = Lists.newArrayList(
      PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
      PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS,
      "extend_obj_data_id", "approve_employee_id"
  );

  @Override
  protected List<IFieldDescribe> getValidImportFields() {
    List<IFieldDescribe> fields = super.getValidImportFields();
    fields.removeIf(f -> REMOVE_FIELDS.contains(f.getApiName()));
    return fields;
  }
}
