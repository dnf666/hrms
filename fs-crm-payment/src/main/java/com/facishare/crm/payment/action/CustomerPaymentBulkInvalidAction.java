package com.facishare.crm.payment.action;

import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import java.util.ArrayList;
import java.util.List;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class CustomerPaymentBulkInvalidAction extends StandardBulkInvalidAction {

  private CustomerPaymentService customerPaymentService =
      SpringUtil.getContext().getBean(CustomerPaymentService.class);

  private List<IObjectData> customerPaymentList;

  @Override
  protected void before(Arg arg) {
    log.info("Bulk invaliding CustomerPayment: {}", arg);
    super.before(arg);
    if (CollectionUtils.isNotEmpty(objectDataList)) {
      customerPaymentList = new ArrayList<>(objectDataList);
      log.debug("Bulk invaliding CustomerPayment: {}", customerPaymentList);
    }
  }

  @Override
  @Transactional
  public Result doAct(Arg arg) {
    Result result = super.doAct(arg);
    if ( CollectionUtils.isNotEmpty(objectDataList) ) {
      for (IObjectData data : objectDataList) {
        customerPaymentService.updateOrderPayment(data, actionContext.getUser());
      }
    }
    customerPaymentService.invalidCustomerAccount(actionContext.getRequestContext(),
        customerPaymentList);
    return result;
  }
}

