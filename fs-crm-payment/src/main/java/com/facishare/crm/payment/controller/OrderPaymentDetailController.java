package com.facishare.crm.payment.controller;


import static com.facishare.crm.payment.utils.JsonPaths.DETAIL_LAYOUT_BUTTONS;

import com.facishare.crm.payment.utils.JsonObjectUtils;
import com.facishare.crm.payment.utils.JsonPaths;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;

public class OrderPaymentDetailController extends StandardDetailController {

  @Override
  protected Result doService(Arg arg) {
    Result result = super.doService(arg);
    result = JsonObjectUtils.remove(result, Result.class,
        DETAIL_LAYOUT_BUTTONS + "[?(@.action=='Abolish')]");
    result = JsonObjectUtils.remove(result, Result.class,
        DETAIL_LAYOUT_BUTTONS + "[?(@.action=='Lock')]");
    result = JsonObjectUtils.remove(result, Result.class,
        DETAIL_LAYOUT_BUTTONS + "[?(@.action=='Edit')]");
    result = JsonObjectUtils.remove(result, Result.class,
        DETAIL_LAYOUT_BUTTONS + "[?(@.action=='Clone')]");
    result = JsonObjectUtils.remove(result, Result.class, JsonPaths.DETAIL_RELATED_OBJECT_REBATE);
    result = JsonObjectUtils.remove(result, Result.class, JsonPaths.DETAIL_RELATED_OBJECT_PREPAY);
    return result;
  }
}
