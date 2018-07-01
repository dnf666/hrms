package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageQuoteFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = -2705363967354561123L;

  public CrmPackageQuoteFieldDescribe(String apiName, String label, boolean isRequired, boolean isReadOnly,
      String quoteField, String quoteFieldType, Map<String, Object> config) {
    super(apiName, "quote", label, isRequired, isReadOnly, ImmutableMap.of(
        "quote_field", quoteField,
        "quote_field_type", quoteFieldType
    ), config);
  }

  public CrmPackageQuoteFieldDescribe(String apiName, String label, boolean isRequired, boolean isReadOnly,
      String quoteField, String quoteFieldType) {
    super(apiName, "quote", label, isRequired, isReadOnly, ImmutableMap.of(
        "quote_field", quoteField,
        "quote_field_type", quoteFieldType
    ), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new QuoteFieldDescribe(document);
  }
}
