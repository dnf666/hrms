package com.facishare.crm.payment.initialize;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrmPackageLayoutFieldSectionDescribe {

  private String apiName;
  private String header;
  private List<BaseFieldDescribe> fields;

  public CrmPackageLayoutFieldSectionDescribe(String apiName, String header,
      List<BaseFieldDescribe> fields) {
    this.apiName = apiName;
    this.header = header;
    this.fields = fields;
  }

  public Map<String, Object> toMap() {
    return ImmutableMap.of(
        "api_name", apiName,
        "header", header,
        "column", 2,
        "is_shown", true,
        "form_fields", fields.stream().map(f -> ImmutableMap.of(
            "is_readonly", f.isReadOnly(),
            "is_required", f.isRequired(),
            "render_type", f.getType(),
            "field_name", f.getApiName()
        )).collect(Collectors.toList())
    );
  }

  public Map<String, Object> toTopMap() {
    return ImmutableMap.of(
        "api_name", apiName,
        "form_fields", fields.stream().map(f -> ImmutableMap.of(
            "field_name", f.getApiName()
        )).collect(Collectors.toList())
    );
  }
}
