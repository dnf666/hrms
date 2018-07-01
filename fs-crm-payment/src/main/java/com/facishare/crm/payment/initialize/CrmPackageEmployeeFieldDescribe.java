package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.EmployeeFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageEmployeeFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = 3977233884599244015L;

  public CrmPackageEmployeeFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, boolean isSingle, Map<String, Object> config) {
    super(apiName, "employee", label, isRequired, isReadOnly, ImmutableMap.of(
        "is_single", isSingle), config);
  }

  public CrmPackageEmployeeFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, boolean isSingle) {
    super(apiName, "employee", label, isRequired, isReadOnly, ImmutableMap.of(
        "is_single", isSingle), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new EmployeeFieldDescribe(document);
  }
}
