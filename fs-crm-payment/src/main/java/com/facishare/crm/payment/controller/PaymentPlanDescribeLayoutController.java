package com.facishare.crm.payment.controller;

import com.facishare.crm.customeraccount.predefine.service.CustomerAccountService;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.utils.FieldUtils;
import com.facishare.crm.payment.utils.JsonObjectUtils;
import com.facishare.crm.payment.utils.JsonPaths;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.metadata.util.SpringUtil;


public class PaymentPlanDescribeLayoutController extends StandardDescribeLayoutController {

  private CustomerAccountService customerAccountService =
      SpringUtil.getContext().getBean(CustomerAccountService.class);

  @Override
  protected Result doService(Arg arg) {
    Result result = super.doService(arg);
    if ("add".equals(arg.getLayout_type()) || "edit".equals(arg.getLayout_type())) {
      result = JsonObjectUtils.remove(result, Result.class,
          JsonPaths.DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
              + PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS + "')]");
      result = JsonObjectUtils.remove(result, Result.class,
          JsonPaths.DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
              + PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT + "')]");
      result = JsonObjectUtils.remove(result, Result.class,
          JsonPaths.DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
              + PaymentPlanObj.FIELD_ORDER_AMOUNT + "')]");

      ServiceContext serviceContext =
          new ServiceContext(controllerContext.getRequestContext(), null, null);
      CustomerAccountType.IsCustomerAccountEnableResult customerAccount =
          customerAccountService.isCustomerAccountEnable(serviceContext);
      if (!customerAccount.isEnable()) {
        result = JsonObjectUtils.remove(result, Result.class,
            JsonPaths.DESCRIBE_LAYOUT_LIST_FIELDS + "." + PaymentPlanObj.FIELD_PLAN_PAYMENT_METHOD
                + ".options" + "[?(@.label=='" + CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT_LABEL
                + "')]");
        result = JsonObjectUtils.remove(result, Result.class,
            JsonPaths.DESCRIBE_LAYOUT_LIST_FIELDS + "." + PaymentPlanObj.FIELD_PLAN_PAYMENT_METHOD
                + ".options" + "[?(@.label=='" + CustomerPaymentObj.PAYMENT_METHOD_REBATE_LABEL
                + "')]");
        result = JsonObjectUtils.remove(result, Result.class,
            JsonPaths.DESCRIBE_LAYOUT_LIST_FIELDS + "." + PaymentPlanObj.FIELD_PLAN_PAYMENT_METHOD
                + ".options" + "[?(@.label=='" + CustomerPaymentObj.PAYMENT_METHOD_DNR_LABEL
                + "')]");
      }
    }

    if ("edit".equals(arg.getLayout_type())) {
      result = JsonObjectUtils.update(result, Result.class,
          JsonPaths.DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
              + PaymentPlanObj.FIELD_ORDER_ID + "')]", FieldUtils
              .buildLayoutField(PaymentPlanObj.FIELD_ORDER_ID, true, true, "object_reference"));
      result = JsonObjectUtils.update(result, Result.class,
          JsonPaths.DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
              + PaymentPlanObj.FIELD_ACCOUNT_ID + "')]", FieldUtils
              .buildLayoutField(PaymentPlanObj.FIELD_ACCOUNT_ID, true, true, "object_reference"));
    }
    return result;
  }

}
