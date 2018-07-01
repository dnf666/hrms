package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public abstract class BaseFieldDescribe implements Serializable {

  private static final long serialVersionUID = -1678470350178594975L;

  private String apiName;
  private String type;
  private String label;
  private String description;
  private boolean isRequired;
  private boolean isReadOnly;
  private boolean isIndex = true;
  private Map<String, Object> config;
  private AbstractFieldDescribe field;

  protected BaseFieldDescribe(String apiName, String type, String label, String description,
      boolean isRequired, boolean isReadOnly, Map<String, Object> properties,
      Map<String, Object> config) {
    this.apiName = apiName;
    this.type = type;
    this.label = label;
    this.description = description;
    this.isRequired = isRequired;
    this.isReadOnly = isReadOnly;
    if (null == config) {
      this.config = new HashMap<>();
    } else {
      this.config = config;
    }
    this.field = initializeField(properties);
  }

  public static Map<String, Object> generateConfig(boolean canEdit, boolean canEnable,
      boolean canRemove, List<String> editableAttributes, List<String> readonlyAttributes) {
    Map<String, Object> config = new HashMap<>();
    if (canEdit) {
      config.put("edit", 1);
    }
    if (canEnable) {
      config.put("enable", 1);
    }
    if (canRemove) {
      config.put("remove", 1);
    }
    Map<String, Object> attributes = new HashMap<>();
    if (CollectionUtils.isNotEmpty(readonlyAttributes)) {
      for (String attribute : readonlyAttributes) {
        attributes.put(attribute, 0);
      }
    }
    if (CollectionUtils.isNotEmpty(editableAttributes)) {
      for (String attribute : editableAttributes) {
        attributes.put(attribute, 1);
      }
    }
    config.put("attrs", attributes);
    return config;
  }

  protected AbstractFieldDescribe initializeField(Map<String, Object> properties) {
    Map<String, Object> document = new HashMap<>(generateDefaultDataMap());
    if (null != properties && !properties.isEmpty()) {
      document.putAll(properties);
    }
    return generateFieldDescribe(document);
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }

  public void setReadOnly(boolean readOnly) {
    isReadOnly = readOnly;
  }

  public AbstractFieldDescribe getField() {
    return field;
  }

  public void setField(AbstractFieldDescribe field) {
    this.field = field;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

  private Map<String, Object> generateDefaultDataMap() {
    Map<String, Object> map = new HashMap<>(ImmutableMap.of(
        IFieldDescribe.API_NAME, apiName,
        IFieldDescribe.LABEL, label,
        IFieldDescribe.DESCRIPTION, description,
        IFieldDescribe.IS_REQUIRED, isRequired,
        IFieldDescribe.CONFIG, config
    ));
    map.put(IFieldDescribe.IS_INDEX, isIndex);
    map.putAll(generateDataMap());
    return map;
  }

  abstract Map<String, Object> generateDataMap();

  abstract AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document);
}
