package com.facishare.crm.payment.initialize;

import com.facishare.paas.metadata.impl.describe.AbstractFieldDescribe;
import com.facishare.paas.metadata.impl.describe.MasterDetailFieldDescribe;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class CrmPackageMasterFieldDescribe extends CrmPackageFieldDescribe {

  public CrmPackageMasterFieldDescribe(String apiName, String label,
      boolean isRequired, boolean isReadOnly, String targetApiName, String targetRelatedListName,
      String targetRelatedListLabel, boolean isCreateWhenMasterCreate,
      boolean isRequiredWhenMasterCreate, Map<String, Object> config) {
    super(apiName, "master_detail", label, isRequired, isReadOnly, ImmutableMap.of(
        "target_api_name", targetApiName,
        "target_related_list_name", targetRelatedListName,
        "target_related_list_label", targetRelatedListLabel,
        "is_create_when_master_create", isCreateWhenMasterCreate,
        "is_required_when_master_create", isRequiredWhenMasterCreate
    ), config);
  }

  public CrmPackageMasterFieldDescribe(String apiName, String label,
      boolean isRequired, boolean isReadOnly, String targetApiName, String targetRelatedListName,
      String targetRelatedListLabel, boolean isCreateWhenMasterCreate,
      boolean isRequiredWhenMasterCreate) {
    super(apiName, "master_detail", label, isRequired, isReadOnly, ImmutableMap.of(
        "target_api_name", targetApiName,
        "target_related_list_name", targetRelatedListName,
        "target_related_list_label", targetRelatedListLabel,
        "is_create_when_master_create", isCreateWhenMasterCreate,
        "is_required_when_master_create", isRequiredWhenMasterCreate
    ), null);
  }

  @Override
  AbstractFieldDescribe generateFieldDescribe(Map<String, Object> document) {
    return new MasterDetailFieldDescribe(document);
  }
}
