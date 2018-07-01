package com.facishare.crm.payment.action;

import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomerPaymentInsertImportDataAction extends StandardInsertImportDataAction {

  @Override
  protected void customValidate(List<ImportData> dataList) {
    super.customValidate(dataList);
    List<ImportError> errorList = Lists.newArrayList();
    log.debug("CustomerPaymentInsertImportDataAction customValidate dataList: {}", dataList);
    ArrayList< String > options = Lists.newArrayList(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
        CustomerPaymentObj.PAYMENT_METHOD_REBATE, CustomerPaymentObj.PAYMENT_METHOD_DNR);
    dataList.forEach(data -> {
      String method = (String) data.getData().get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
      if (StringUtils.isNotBlank(method) && options.contains(method)){
        errorList.add(new ImportError(data.getRowNo(), "回款方式不能为预存款、返利、预存款+返利"));
      }
    });
    mergeErrorList(errorList);
  }
}
