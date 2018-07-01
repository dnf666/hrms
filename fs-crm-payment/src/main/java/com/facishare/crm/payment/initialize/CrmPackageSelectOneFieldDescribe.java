package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class CrmPackageSelectOneFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = 1071846368548475338L;

  public CrmPackageSelectOneFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, List<SelectOneOption> options, String defaultValue) {
    super(apiName, "select_one", label, isRequired, isReadOnly,
        StringUtils.isBlank(defaultValue) ? ImmutableMap.of(
            "options", options.stream().map(o -> ImmutableMap.of(
                "label", o.label,
                "value", o.value,
                "not_usable", false,
                "config", generateConfig(o.readonly)
            )).collect(Collectors.toList())) :
            ImmutableMap.of(
                "options", options.stream().map(o -> ImmutableMap.of(
                    "label", o.label,
                    "value", o.value,
                    "not_usable", false,
                    "config", generateConfig(o.readonly)
                )).collect(Collectors.toList()),
                "default_value", defaultValue
            ), null);
  }

  public CrmPackageSelectOneFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, List<SelectOneOption> options, String defaultValue, Map<String, Object> config) {
    super(apiName, "select_one", label, isRequired, isReadOnly,
        StringUtils.isBlank(defaultValue) ? ImmutableMap.of(
            "options", options.stream().map(o -> ImmutableMap.of(
                "label", o.label,
                "value", o.value,
                "not_usable", false,
                "config", generateConfig(o.readonly)
            )).collect(Collectors.toList())) :
            ImmutableMap.of(
                "options", options.stream().map(o -> ImmutableMap.of(
                    "label", o.label,
                    "value", o.value,
                    "not_usable", false,
                    "config", generateConfig(o.readonly)
                )).collect(Collectors.toList()),
                "default_value", defaultValue
            ), generateFieldConfig(config));
  }

  private static Map<String, Object> generateConfig(boolean readonly) {
    return readonly ? new HashMap<>() : ImmutableMap.of("edit", 1, "remove", 1, "enable", 1);
  }

  private static Map<String, Object> generateFieldConfig(Map<String, Object> config) {
    if ( (int)config.getOrDefault("edit", 0) == 1 ) {
      config.put("add", 1);
      config.put("attrs", ImmutableMap.of("options", 1));
    }
    return config;
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new SelectOneFieldDescribe(document);
  }

  public static class SelectOneOption {

    private String label;
    private String value;
    private boolean readonly = true;

    public SelectOneOption(String label, String value) {
      this.label = label;
      this.value = value;
    }

    public SelectOneOption(String label, String value, boolean readonly) {
      this.label = label;
      this.value = value;
      this.readonly = readonly;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public boolean isReadonly() {
      return readonly;
    }

    public void setReadonly(boolean readonly) {
      this.readonly = readonly;
    }
  }
}
