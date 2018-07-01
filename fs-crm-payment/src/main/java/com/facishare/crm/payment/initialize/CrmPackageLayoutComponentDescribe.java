package com.facishare.crm.payment.initialize;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrmPackageLayoutComponentDescribe {

  private String apiName;
  private String type;
  private String referenceApiName;
  private List<CrmPackageLayoutFieldSectionDescribe> sections;
  private List<BaseFieldDescribe> fields;

  public CrmPackageLayoutComponentDescribe(String apiName, String type,
      List<CrmPackageLayoutFieldSectionDescribe> sections) {
    this.apiName = apiName;
    this.type = type;
    this.sections = sections;
  }

  public CrmPackageLayoutComponentDescribe(String apiName, String type, String referenceApiName, List<BaseFieldDescribe> fields) {
    this.apiName = apiName;
    this.type = type;
    this.referenceApiName = referenceApiName;
    this.fields = fields;
  }

  public Map<String, Object> toDetailMap() {
    return ImmutableMap.of(
        "api_name", apiName,
        "type", type,
        "field_section", sections.stream().map(CrmPackageLayoutFieldSectionDescribe::toMap)
            .collect(Collectors.toList())
    );
  }

  public Map<String, Object> toListMap() {
    return ImmutableMap.of(
        "api_name", apiName,
        "type", type,
        "buttons", Lists.newArrayList(),
        "ref_object_api_name", referenceApiName,
        "include_fields", fields.stream().map(f -> ImmutableMap.of(
            "api_name", f.getApiName(),
            "label", f.getLabel(),
            "render_type", f.getType()
        )).collect(Collectors.toList())
    );
  }
}
