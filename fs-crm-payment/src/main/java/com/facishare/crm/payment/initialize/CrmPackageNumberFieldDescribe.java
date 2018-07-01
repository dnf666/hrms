package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.NumberFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageNumberFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = -2084543328589496669L;

  public CrmPackageNumberFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, Map<String, Object> config) {
    super(apiName, "number", label, isRequired, isReadOnly, ImmutableMap.of(
        "length", 14
    ), config);
  }

  public CrmPackageNumberFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly) {
    super(apiName, "number", label, isRequired, isReadOnly, ImmutableMap.of(
        "length", 14
    ), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new NumberFieldDescribe(document);
  }
}
