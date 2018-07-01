package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.CountFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageCountFieldDescribe extends CrmPackageFieldDescribe {

  public CrmPackageCountFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String countType, String detailApiName, String detailFieldApiName,
      String detailFieldReferenceApiName, String detailFieldDataType, Map<String, Object> extension,
      Map<String, Object> config) {
    super(apiName, "count", label, isRequired, isReadOnly, ImmutableMap.of(
        "count_type", countType,
        "sub_object_describe_apiname", detailApiName,
        "count_field_api_name", detailFieldApiName,
        "count_field_type", detailFieldDataType,
        "field_api_name", detailFieldReferenceApiName
    ), config);
    for (Map.Entry<String, Object> entry : extension.entrySet()) {
      getField().set(entry.getKey(), entry.getValue());
    }
  }

  public CrmPackageCountFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String countType, String detailApiName, String detailFieldApiName,
      String detailFieldDataType, Map<String, Object> extension) {
    super(apiName, "count", label, isRequired, isReadOnly, ImmutableMap.of(
        "count_type", countType,
        "sub_object_describe_apiname", detailApiName,
        "count_field_api_name", detailFieldApiName,
        "count_field_type", detailFieldDataType
    ), null);
    for (Map.Entry<String, Object> entry : extension.entrySet()) {
      getField().set(entry.getKey(), entry.getValue());
    }
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new CountFieldDescribe(document);
  }
}
