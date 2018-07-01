package com.facishare.crm.payment.action;

import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CustomerPaymentBulkDeleteAction extends StandardBulkDeleteAction {

  private CustomerPaymentService customerPaymentService =
          SpringUtil.getContext().getBean(CustomerPaymentService.class);

  @Override
  protected Result doAct(Arg arg) {
    SfaOrderPaymentModel.BulkDeleteArg deleteArg = bulidOrderPaymentMap(arg.getIdList());
    Result result = super.doAct(arg);
    if (result.getSuccess()) {
      customerPaymentService.bulkDelete(deleteArg, this.actionContext);
    }
    return result;
  }

  private SfaOrderPaymentModel.BulkDeleteArg bulidOrderPaymentMap(List<String> paymentIds) {
    Map<String, List<String>> map = Maps.newHashMap();
    ServiceContext serviceContext = new ServiceContext(actionContext.getRequestContext(), null,
            null);
    paymentIds.forEach(x -> {
      List<Map> maps = customerPaymentService.queryOrderPaymentList(serviceContext, x);
      List<String> ids = maps.stream()
              .map(y -> y.get(OrderPaymentObj.FIELD_ID).toString()).collect(Collectors.toList());
      map.put(x, ids);
    });
    SfaOrderPaymentModel.BulkDeleteArg deleteArg = new SfaOrderPaymentModel.BulkDeleteArg();
    deleteArg.setOrderPaymentMap(map);
    return deleteArg;

  }
}
