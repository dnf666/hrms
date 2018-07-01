package com.facishare.crm.payment.action;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardExportAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderPaymentExportAction extends StandardExportAction {

  private CustomerPaymentService service = SpringUtil.getContext()
          .getBean(CustomerPaymentService.class);

  @Override
  protected Result doAct(Arg arg) {
    return super.doAct(arg);
  }

  @Override
  protected Map<String, List<IObjectData>> generateDataMap() {
    Map<String, List<IObjectData>> dataMap = super.generateDataMap();
    dataMap.put(PaymentObject.ORDER_PAYMENT.getApiName(),parseDateTime(objectDescribe,dataMap.get(PaymentObject.ORDER_PAYMENT.getApiName())));
    return dataMap;
  }

  private List<IObjectData> parseDateTime(IObjectDescribe objectDescribe, List<IObjectData> data) {
    return ObjectDataDocument
            .ofDataList(service.parseDateTime(objectDescribe, data.stream().map(
                    ObjectDataDocument::of).collect(Collectors.toList())));
  }
}
