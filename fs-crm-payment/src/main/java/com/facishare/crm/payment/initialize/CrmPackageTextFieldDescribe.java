package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.TextFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageTextFieldDescribe extends CrmPackageFieldDescribe {

  public CrmPackageTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, int length, boolean isIndex, Map<String, Object> config) {
    super(apiName, "text", label, isRequired, isReadOnly, ImmutableMap.of(
        "max_length", length, "is_index", isIndex), config);
  }

  public CrmPackageTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, int length, Map<String, Object> config) {
    super(apiName, "text", label, isRequired, isReadOnly, ImmutableMap.of(
        "max_length", length), config);
  }

  public CrmPackageTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, int length) {
    super(apiName, "text", label, isRequired, isReadOnly, ImmutableMap.of(
        "max_length", length), null);
  }

  public CrmPackageTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, boolean isSingle, int length, Map<String, Object> config) {
    super(apiName, "text", label, isRequired, isReadOnly, ImmutableMap.of(
        "is_single", isSingle,
        "max_length", length
    ), config);
  }

  public CrmPackageTextFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, boolean isSingle, int length) {
    super(apiName, "text", label, isRequired, isReadOnly, ImmutableMap.of(
        "is_single", isSingle,
        "max_length", length
    ), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new TextFieldDescribe(document);
  }
}
