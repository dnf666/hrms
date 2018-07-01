package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateTimeFieldDescribe;
import java.util.Map;

public class CrmPackageDateTimeFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = 1030786311457044886L;

  public CrmPackageDateTimeFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, Map<String, Object> config) {
    super(apiName, "date_time", label, isRequired, isReadOnly, null, config);
  }

  public CrmPackageDateTimeFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly) {
    super(apiName, "date_time", label, isRequired, isReadOnly, null, null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new DateTimeFieldDescribe(document);
  }
}
