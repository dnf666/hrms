package com.facishare.crm.payment.initialize;

import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INEFFECTIVE;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INEFFECTIVE_LABEL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INVALID;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_INVALID_LABEL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_IN_CHANGE;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_IN_CHANGE_LABEL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_NORMAL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_NORMAL_LABEL;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_UNDER_REVIEW;
import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_VALUE_UNDER_REVIEW_LABEL;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.payment.initialize.CrmPackageSelectOneFieldDescribe.SelectOneOption;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CrmPackageObjectDescribe extends BaseObjectDescribe {

  protected static CrmPackageFieldDescribe owner = new CrmPackageEmployeeFieldDescribe(
      "owner", "负责人", true, false, true);
  protected static CrmPackageFieldDescribe ownerDepartment = new CrmPackageTextFieldDescribe(
      "owner_department", "负责人所在部门", false, false, true, 255);
  protected static CrmPackageFieldDescribe recordType = new CrmPackageRecordTypeFieldDescribe(
      "record_type", "业务类型", true, false);
  protected static CrmPackageFieldDescribe extendObjectDataId = new CrmPackageTextFieldDescribe(
      "extend_obj_data_id", "extend_obj_data_id", false, false, 100);
  protected static CrmPackageFieldDescribe attachment = new CrmPackageAttachmentFieldDescribe(
      "attachment", "附件", false, false, 1,
      BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label", "file_amount_limit"), null));
  protected static CrmPackageFieldDescribe remark = new CrmPackageLongTextFieldDescribe(
      "remark", "备注",
      false, false, BaseFieldDescribe.generateConfig(true, true, true, Lists.newArrayList("label"), null));
  protected static CrmPackageFieldDescribe lifeStatus = new CrmPackageSelectOneFieldDescribe(
      "life_status", "生命状态", true, false, Lists.newArrayList(
      new SelectOneOption(LIFE_STATUS_VALUE_INEFFECTIVE_LABEL, LIFE_STATUS_VALUE_INEFFECTIVE),
      new SelectOneOption(LIFE_STATUS_VALUE_UNDER_REVIEW_LABEL, LIFE_STATUS_VALUE_UNDER_REVIEW),
      new SelectOneOption(LIFE_STATUS_VALUE_NORMAL_LABEL, LIFE_STATUS_VALUE_NORMAL),
      new SelectOneOption(LIFE_STATUS_VALUE_IN_CHANGE_LABEL, LIFE_STATUS_VALUE_IN_CHANGE),
      new SelectOneOption(LIFE_STATUS_VALUE_INVALID_LABEL, LIFE_STATUS_VALUE_INVALID)
  ), LIFE_STATUS_VALUE_NORMAL);
  protected static CrmPackageFieldDescribe sysApproveEmployeeId = new CrmPackageEmployeeFieldDescribe("approve_employee_id", "当前审批人", false, true, false);

  private Map<String, Object> detailLayout;
  private Map<String, Object> listLayout;
  private String name;
  private String description;

  CrmPackageObjectDescribe(String apiName, String name, String tableName, String description) {
    super(apiName);
    this.name = name;
    this.description = description;
    setTableName(tableName);
    initializeDataMap();
    initializeDetailLayout();
    initializeListLayout();
  }

  private void initializeDataMap() {
    this.data = Maps.newLinkedHashMap();
    data.put("api_name", getApiName());
    data.put("display_name", name);
    data.put("description", description);
    data.put("package", "CRM");
    data.put("define_type", "package");
    data.put("store_table_name", getTableName());
    data.put("fields", initializeFields().stream().collect(Collectors.toMap(
        f -> f.getField().getApiName(), f -> f.getField().getContainerDocument(),
        (u, v) -> {
          throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new)));
  }

  @SuppressWarnings("unchecked")
  private void initializeDetailLayout() {
    this.detailLayout = Maps.newHashMap();
    detailLayout.put("api_name", getDetailLayoutApiName());
    detailLayout.put("layout_description", "");
    detailLayout.put("display_name", "默认布局");
    detailLayout.put("is_default", true);
    detailLayout.put("layout_type", "detail");
    detailLayout.put("default_component", "form_component");
    detailLayout.put("package", "CRM");
    detailLayout.put("ref_object_api_name", getApiName());
    detailLayout.put("components", Lists.newArrayList(
        new CrmPackageLayoutComponentDescribe("form_component", "form", Lists.newArrayList(
            new CrmPackageLayoutFieldSectionDescribe("base_field_section__c", "基本信息",
                initializeDetailLayoutFields()),
            new CrmPackageLayoutFieldSectionDescribe("system_field_section__c", "系统信息",
                initializeDetailLayoutSystemFields())
        )).toDetailMap()
    ));
    detailLayout.put("top_info", initializeTopLayout());
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> initializeTopLayout() {
    return ImmutableMap.of(
        "type", "simple",
        "api_name", "top_info",
        "header", "顶部信息",
        "field_section", Lists.newArrayList(
            new CrmPackageLayoutFieldSectionDescribe("detail", "顶部信息", initializeTopLayoutFields()).toTopMap()
        )
    );
  }

  @SuppressWarnings("unchecked")
  private void initializeListLayout() {
    this.listLayout = Maps.newHashMap();
    listLayout.put("api_name", getListLayoutApiName());
    listLayout.put("layout_description", "");
    listLayout.put("display_name", "移动端默认列表页");
    listLayout.put("is_default", false);
    listLayout.put("layout_type", "list");
    listLayout.put("package", "CRM");
    listLayout.put("agent_type", "agent_type_mobile");
    listLayout.put("ref_object_api_name", getApiName());
    listLayout.put("components", Lists.newArrayList(
        new CrmPackageLayoutComponentDescribe("table_component", "table", getApiName(),
            initializeListLayoutFields()).toListMap()
    ));
  }

  public abstract LinkedList<BaseFieldDescribe> initializeFields();

  abstract LinkedList<BaseFieldDescribe> initializeDetailLayoutFields();

  protected List<BaseFieldDescribe> initializeDetailLayoutSystemFields() {
    return Lists.newArrayList(
        new CrmPackageEmployeeFieldDescribe("created_by", "创建人", false, true, true),
        new CrmPackageDateTimeFieldDescribe("create_time", "创建时间", false, true),
        new CrmPackageDateTimeFieldDescribe("last_modified_time", "修改时间", false, true),
        new CrmPackageEmployeeFieldDescribe("last_modified_by", "修改人", false, true, true)
    );
  }

  abstract LinkedList<BaseFieldDescribe> initializeListLayoutFields();

  abstract LinkedList<BaseFieldDescribe> initializeTopLayoutFields();

  @Override
  public String getDataJson() {
    return JSON.toJSONString(this.data);
  }

  @Override
  public String getListLayoutJson() {
    return JSON.toJSONString(this.listLayout);
  }

  @Override
  public String getDetailLayoutJson() {
    return JSON.toJSONString(this.detailLayout);
  }
}
