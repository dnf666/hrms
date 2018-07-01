package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import java.util.Map;

public class CrmPackageRecordTypeFieldDescribe extends CrmPackageFieldDescribe {

  public CrmPackageRecordTypeFieldDescribe(String apiName, String label,
      boolean isRequired, boolean isReadOnly, Map<String, Object> config) {
    super(apiName, "record_type", label, isRequired, isReadOnly, null, config);
  }

  public CrmPackageRecordTypeFieldDescribe(String apiName, String label,
      boolean isRequired, boolean isReadOnly) {
    super(apiName, "record_type", label, isRequired, isReadOnly, null, null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new RecordTypeFieldDescribe(document);
  }
}
