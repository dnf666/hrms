package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.CurrencyFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageCurrencyFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = -6339874344630796181L;

  public CrmPackageCurrencyFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, Map<String, Object> config) {
    super(apiName, "currency", label, isRequired, isReadOnly, null, config);
  }

  public CrmPackageCurrencyFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly) {
    super(apiName, "currency", label, isRequired, isReadOnly, null, null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    document.put("decimal_places", "2");
    return new CurrencyFieldDescribe(document);
  }
}
