package com.facishare.crm.payment.initialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseObjectDescribe implements Serializable {

  private String apiName;
  private boolean active = true;
  private boolean includeLayout = true;
  private String dbType = "pg";
  private boolean slotCreated = false;
  private Integer version;
  private String tableName;

  protected Map<String, Object> data;

  protected List<BaseRuleDescribe> rules = new ArrayList<>();

  BaseObjectDescribe(String apiName) {
    this.apiName = apiName;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isIncludeLayout() {
    return includeLayout;
  }

  public void setIncludeLayout(boolean includeLayout) {
    this.includeLayout = includeLayout;
  }

  public String getDbType() {
    return dbType;
  }

  public void setDbType(String dbType) {
    this.dbType = dbType;
  }

  public boolean isSlotCreated() {
    return slotCreated;
  }

  public void setSlotCreated(boolean slotCreated) {
    this.slotCreated = slotCreated;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public List<BaseRuleDescribe> getRules() {
    return rules;
  }

  public void setRules(List<BaseRuleDescribe> rules) {
    this.rules = rules;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public abstract String getDataJson();

  public abstract String getListLayoutJson();

  public abstract String getDetailLayoutJson();

  public String getDetailLayoutApiName() {
    return "detail_layout_" + getApiName().toLowerCase() + "__c";
  }

  public String getListLayoutApiName() {
    return "list_layout_" + getApiName().toLowerCase() + "__c";
  }

}
