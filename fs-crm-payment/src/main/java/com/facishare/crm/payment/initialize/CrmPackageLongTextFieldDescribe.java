package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.LongTextFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageLongTextFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = -3771866602066714411L;

  public CrmPackageLongTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, Map<String, Object> config) {
    super(apiName, "long_text", label, isRequired, isReadOnly, ImmutableMap.of(
        "max_length", 2000), config);
  }

  public CrmPackageLongTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly) {
    super(apiName, "long_text", label, isRequired, isReadOnly, ImmutableMap.of(
        "max_length", 2000), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new LongTextFieldDescribe(document);
  }
}
