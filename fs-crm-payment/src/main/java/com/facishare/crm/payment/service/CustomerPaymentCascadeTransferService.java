package com.facishare.crm.payment.service;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.fxiaoke.transfer.dto.RequestData;
import org.springframework.stereotype.Component;

@ServiceModule("cpcascadeselecttransfer")
@Component
public class CustomerPaymentCascadeTransferService extends CascadeSelectTransferService {

  public static final String TRANSFER_HOOK_PATH = "/API/v1/inner/object/cpcascadeselecttransfer/service/transfer";
  public static final String QUERY_SQL = "select md.* from mt_data md INNER JOIN payment_customer p ON p.extend_obj_data_id = md.id and md.tenant_id = ${ei}";

  @Override
  protected String getDescribeApiName() {
    return PaymentObject.CUSTOMER_PAYMENT.getApiName();
  }

  @ServiceMethod("transfer")
  public boolean doTransfer(RequestData requestData) {
    transfer(requestData);
    return true;
  }
}
