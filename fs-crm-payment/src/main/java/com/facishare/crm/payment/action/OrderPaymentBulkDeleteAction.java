package com.facishare.crm.payment.action;


import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
public class OrderPaymentBulkDeleteAction extends StandardBulkDeleteAction {

  private CustomerPaymentService customerPaymentService =
      SpringUtil.getContext().getBean(CustomerPaymentService.class);

  @Override
  @Transactional
  protected Result doAct(Arg arg) {
    log.debug("OrderPaymentBulkDeleteAction doAct arg:{}", arg);
    SfaOrderPaymentModel.BulkDeleteArg deleteArg =
        customerPaymentService.buildBulkDeleteSyncAccountInfoArg(actionContext, arg);
    Result result = super.doAct(arg);
    log.debug("OrderPaymentBulkDeleteAction doAct result:{}", result);
    if (result.getSuccess()) {
      customerPaymentService.bulkDelete(deleteArg, this.actionContext);
    }
    return result;
  }
}

