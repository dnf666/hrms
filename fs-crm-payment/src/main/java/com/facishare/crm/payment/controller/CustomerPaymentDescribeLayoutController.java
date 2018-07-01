package com.facishare.crm.payment.controller;

import com.facishare.crm.customeraccount.predefine.service.CustomerAccountService;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.utils.JsonObjectUtils;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facishare.crm.payment.utils.FieldUtils.buildCurrencyFieldMap;
import static com.facishare.crm.payment.utils.FieldUtils.buildLayoutField;
import static com.facishare.crm.payment.utils.JsonPaths.*;

public class CustomerPaymentDescribeLayoutController extends StandardDescribeLayoutController {
  @Override
  protected Result doService(Arg arg) {
    Result result = super.doService(arg);
    if ("add".equals(arg.getLayout_type()) || "edit".equals(arg.getLayout_type())) {
      result = modifyResult(result);
    }
    if ("edit".equals(arg.getLayout_type())) {
      result = modifyEditResult(result, arg.getData_id());
    }
    return result;
  }

  private Result modifyEditResult(Result result, String paymentId) {
    IObjectData data = serviceFacade.findObjectData(controllerContext.getUser(), paymentId,
        PaymentObject.CUSTOMER_PAYMENT.getApiName());
    if (data == null) {
      return result;
    }
    String method = (String) data.get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
    ArrayList< String > options = Lists.newArrayList(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
        CustomerPaymentObj.PAYMENT_METHOD_REBATE, CustomerPaymentObj.PAYMENT_METHOD_DNR);
    if (!options.contains(method)) {
      return result;
    }
    Result r = JsonObjectUtils.update(result, Result.class,
        DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
            + CustomerPaymentObj.FIELD_ACCOUNT_ID + "')].is_readonly", true);
    r = JsonObjectUtils.update(r, Result.class,
        CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUT_FIELDS + "[?(@.field_name=='"
            + OrderPaymentObj.FIELD_ORDER_ID + "')].is_readonly", true);
    r = JsonObjectUtils.update(r, Result.class,
        CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUT_FIELDS + "[?(@.field_name=='"
            + OrderPaymentObj.FIELD_PAYMENT_PLAN_ID + "')].is_readonly", true);
    return r;
  }

  private Result modifyResult(Result result) {
    CustomerAccountService customerAccountService =
        SpringUtil.getContext().getBean(CustomerAccountService.class);
    ServiceContext context = new ServiceContext(controllerContext.getRequestContext(), null, null);
    CustomerAccountType.IsCustomerAccountEnableResult customerAccountEnable =
        customerAccountService.isCustomerAccountEnable(context);
    boolean enable = customerAccountEnable.isEnable();
    if (!enable) {
      result = JsonObjectUtils.remove(result, Result.class,
          DESCRIBE_LAYOUT_LIST_FIELDS + "." + CustomerPaymentObj.FIELD_PAYMENT_METHOD
              + ".options[?(@.value=='" + CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT + "')]");
      result = JsonObjectUtils.remove(result, Result.class,
          DESCRIBE_LAYOUT_LIST_FIELDS + "." + CustomerPaymentObj.FIELD_PAYMENT_METHOD
              + ".options[?(@.value=='" + CustomerPaymentObj.PAYMENT_METHOD_REBATE + "')]");
      result = JsonObjectUtils.remove(result, Result.class,
          DESCRIBE_LAYOUT_LIST_FIELDS + "." + CustomerPaymentObj.FIELD_PAYMENT_METHOD
              + ".options[?(@.value=='" + CustomerPaymentObj.PAYMENT_METHOD_DNR + "')]");

    }

    Map< String, Map< String, Object > > append = Maps.newHashMap();
    List< Map< String, Object > > layout = Lists.newArrayList();
    layout.add(buildLayoutField(OrderPaymentObj.PREPAY, false, false, "currency"));
    layout.add(buildLayoutField(OrderPaymentObj.REBATE_OUTCOME, false, false, "currency"));
    append.put(OrderPaymentObj.PREPAY,
        buildCurrencyFieldMap(OrderPaymentObj.PREPAY, OrderPaymentObj.PREPAY_LABEL));
    append.put(OrderPaymentObj.REBATE_OUTCOME, buildCurrencyFieldMap(OrderPaymentObj.REBATE_OUTCOME,
        OrderPaymentObj.REBATE_OUTCOME_LABEL));
    result = JsonObjectUtils.append(result, Result.class, DESCRIBE_DETAIL_DESCRIBE_FIELDS, append);
    List<Map> details = JsonObjectUtils.get(result, List.class, CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUTS);
    if (CollectionUtils.isNotEmpty(details)){
      for (int i = 0; i < details.size(); i++) {
        Map document = details.get(i);
        document = JsonObjectUtils.append(document, Map.class, ORDER_PAYMENT_LAYOUT, layout, true);
        details.set(i, document);
      }
    }
    result = JsonObjectUtils.update(result, Result.class, CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUTS, details);
    result = JsonObjectUtils.remove(result, Result.class,
        DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
            + CustomerPaymentObj.FIELD_APPROVE_EMPLOYEE_ID + "')]");
    result = JsonObjectUtils.remove(result, Result.class,
        DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS + "[?(@.field_name=='"
            + CustomerPaymentObj.FIELD_APPROVE_TIME + "')]");
    result = JsonObjectUtils.remove(result, Result.class,
        CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUT_FIELDS + "[?(@.field_name=='"
            + OrderPaymentObj.FIELD_PAYMENT_METHOD + "')]");
    result = JsonObjectUtils.remove(result, Result.class,
        CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUT_FIELDS + "[?(@.field_name=='"
            + OrderPaymentObj.FIELD_PAYMENT_TIME + "')]");
    result = JsonObjectUtils.remove(result, Result.class,
        CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUT_FIELDS + "[?(@.field_name=='"
            + OrderPaymentObj.FIELD_ACCOUNT_ID + "')]");
    return result;
  }
}
