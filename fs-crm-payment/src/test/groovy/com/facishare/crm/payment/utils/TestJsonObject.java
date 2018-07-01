package com.facishare.crm.payment.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestJsonObject {

  private Integer id;
  private String title;
  private List<TestJsonObject> children = new ArrayList<>();
  private Map<String, Object> properties = new HashMap<>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<TestJsonObject> getChildren() {
    return children;
  }

  public void setChildren(List<TestJsonObject> children) {
    this.children = children;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
