package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public abstract class CrmPackageFieldDescribe extends BaseFieldDescribe {

  private static final long serialVersionUID = -252126038090946917L;

  public CrmPackageFieldDescribe(String apiName, String type, String label, boolean isRequired,
      boolean isReadOnly, Map<String, Object> properties, Map<String, Object> config) {
    super(apiName, type, label, label, isRequired, isReadOnly, properties, config);
  }

  @Override
  Map<String, Object> generateDataMap() {
    return ImmutableMap.of(
        IFieldDescribe.DEFINE_TYPE, IFieldDescribe.DEFINE_TYPE_PACKAGE
    );
  }
}
