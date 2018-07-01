package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.AutoNumberFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmSystemAutoNumberFieldDescribe extends BaseFieldDescribe {

  public CrmSystemAutoNumberFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String prefix, String postfix, int startNumber, int serialNumber, Map<String, Object> config) {
    super(apiName, "auto_number", label, label, isRequired, isReadOnly, ImmutableMap.of(
        "prefix", prefix,
        "postfix", postfix,
        "start_number", startNumber,
        "serial_number", serialNumber
    ), config);
  }

  public CrmSystemAutoNumberFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String prefix, String postfix, int startNumber, int serialNumber) {
    super(apiName, "auto_number", label, label, isRequired, isReadOnly, ImmutableMap.of(
        "prefix", prefix,
        "postfix", postfix,
        "start_number", startNumber,
        "serial_number", serialNumber
    ), null);
  }

  @Override
  Map<String, Object> generateDataMap() {
    return ImmutableMap.of(
        IFieldDescribe.DEFINE_TYPE, IFieldDescribe.DEFINE_TYPE_SYSTEM
    );
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new AutoNumberFieldDescribe(document);
  }
}
