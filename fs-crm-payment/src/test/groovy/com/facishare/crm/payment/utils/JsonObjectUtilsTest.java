package com.facishare.crm.payment.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonObjectUtilsTest {

  private TestJsonObject jsonObject;

  @Before
  public void setup() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    jsonObject = mapper.readValue(JsonObjectUtilsTest.class.getClassLoader().getResourceAsStream(
        "JsonObjectUtilsTest.json"), TestJsonObject.class);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testAppendToArray() {
    TestJsonObject result = JsonObjectUtils
        .append(jsonObject, TestJsonObject.class, "$.children[?(@.id==1)].properties.options",
            ImmutableMap.of("text", "C", "value", "C"));

    List<Map<String, Object>> options = (List<Map<String, Object>>) result.getChildren().stream()
        .filter(x -> x.getId() == 1).findFirst().orElse(new TestJsonObject()).getProperties().getOrDefault("options",
            Lists.newArrayList());
    Assert.assertTrue(
        options.stream().anyMatch(x -> "C".equals(x.get("value"))));
  }

  @Test
  public void testAppendToMap() {
    TestJsonObject result = JsonObjectUtils
        .append(jsonObject, TestJsonObject.class, "$.children[?(@.id==1)].properties",
            ImmutableMap.of("type", "select"));

    Map<String, Object> properties = result.getChildren().stream()
        .filter(x -> x.getId() == 1).findFirst().orElse(new TestJsonObject()).getProperties();
    Assert.assertTrue(properties.getOrDefault("type", "").equals("select"));
  }

  @Test
  public void testAppendToMap2() {
    TestJsonObject result = JsonObjectUtils
        .append(jsonObject, TestJsonObject.class, "$.properties",
            ImmutableMap.of("type", "select"));

    Map<String, Object> properties = result.getProperties();
    Assert.assertTrue(properties.getOrDefault("type", "").equals("select"));
  }

  @Test
  public void testUpdate() {
    TestJsonObject result = JsonObjectUtils.update(jsonObject, TestJsonObject.class, "$.children[?(@.id==1)].properties.color", "orange");
    Map<String, Object> properties = result.getChildren().stream()
        .filter(x -> x.getId() == 1).findFirst().orElse(new TestJsonObject()).getProperties();
    Assert.assertTrue(properties.getOrDefault("color", "").equals("orange"));
  }

  @Test
  public void testRemove() {
    TestJsonObject result = JsonObjectUtils.remove(jsonObject, TestJsonObject.class, "$.children[?(@.id==1)].properties.color");
    Map<String, Object> properties = result.getChildren().stream()
        .filter(x -> x.getId() == 1).findFirst().orElse(new TestJsonObject()).getProperties();
    Assert.assertFalse(properties.containsKey("color"));
  }
}