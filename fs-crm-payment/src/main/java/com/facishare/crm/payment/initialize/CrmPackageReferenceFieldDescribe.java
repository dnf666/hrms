package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class CrmPackageReferenceFieldDescribe extends CrmPackageFieldDescribe {

  private static final long serialVersionUID = 7200211834433272312L;

  public CrmPackageReferenceFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String targetApiName, String listApiName, String listLabel,
      List<Map<String, Object>> where, String cascadeFieldApiName, Map<String, Object> config) {
    super(apiName, "object_reference", label, isRequired, isReadOnly, ImmutableMap.of(
        "target_api_name", targetApiName,
        "target_related_list_name", listApiName,
        "target_related_list_label", listLabel,
        "wheres", where == null ? Lists.newArrayList() : where,
        "action_on_target_delete", "set_null"
    ), config);
    this.getField().set("cascade_parent_api_name", cascadeFieldApiName);
  }

  public CrmPackageReferenceFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String targetApiName, String listApiName, String listLabel,
      List<Map<String, Object>> where, String cascadeFieldApiName) {
    super(apiName, "object_reference", label, isRequired, isReadOnly, ImmutableMap.of(
        "target_api_name", targetApiName,
        "target_related_list_name", listApiName,
        "target_related_list_label", listLabel,
        "wheres", where == null ? Lists.newArrayList() : where,
        "action_on_target_delete", "set_null"
    ), null);
    this.getField().set("cascade_parent_api_name", cascadeFieldApiName);
  }

  public CrmPackageReferenceFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String targetApiName, String listApiName, String listLabel,
      List<Map<String, Object>> where,Map<String, Object> config) {
    super(apiName, "object_reference", label, isRequired, isReadOnly, ImmutableMap.of(
        "target_api_name", targetApiName,
        "target_related_list_name", listApiName,
        "target_related_list_label", listLabel,
        "wheres", where == null ? Lists.newArrayList() : where,
        "action_on_target_delete", "set_null"
    ), config);
  }

  public CrmPackageReferenceFieldDescribe(String apiName, String label, boolean isRequired,
      boolean isReadOnly, String targetApiName, String listApiName, String listLabel,
      List<Map<String, Object>> where) {
    super(apiName, "object_reference", label, isRequired, isReadOnly, ImmutableMap.of(
        "target_api_name", targetApiName,
        "target_related_list_name", listApiName,
        "target_related_list_label", listLabel,
        "wheres", where == null ? Lists.newArrayList() : where,
        "action_on_target_delete", "set_null"
    ), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new ObjectReferenceFieldDescribe(document);
  }
}
