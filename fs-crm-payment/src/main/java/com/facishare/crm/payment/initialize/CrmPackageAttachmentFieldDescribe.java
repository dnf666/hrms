package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.FileAttachmentFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageAttachmentFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = 2174554881902748986L;

  public CrmPackageAttachmentFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, int fileAmountLimit, Map<String, Object> config) {
    super(apiName, "file_attachment", label, isRequired, isReadOnly, ImmutableMap.of(
        "file_amount_limit", fileAmountLimit
    ), config);
  }

  public CrmPackageAttachmentFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, int fileAmountLimit) {
    super(apiName, "file_attachment", label, isRequired, isReadOnly, ImmutableMap.of(
        "file_amount_limit", fileAmountLimit
    ), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new FileAttachmentFieldDescribe(document);
  }
}
