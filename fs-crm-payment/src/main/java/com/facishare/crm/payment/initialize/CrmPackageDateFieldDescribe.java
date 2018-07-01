package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateFieldDescribe;
import java.util.Map;

public class CrmPackageDateFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = 8724730861994868764L;

  public CrmPackageDateFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, Map<String, Object> config) {
    super(apiName, "date", label, isRequired, isReadOnly, null, config);
  }

  public CrmPackageDateFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly) {
    super(apiName, "date", label, isRequired, isReadOnly, null, null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new DateFieldDescribe(document);
  }
}
