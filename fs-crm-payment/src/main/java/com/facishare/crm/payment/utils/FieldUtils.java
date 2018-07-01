package com.facishare.crm.payment.utils;

import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.search.Wheres;
import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class FieldUtils {

  public static Map<String, Object> buildLayoutField(String name, boolean readonly,
      boolean required, String type) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("field_name", name);
    map.put("is_readonly", readonly);
    map.put("is_required", required);
    map.put("render_type", type);
    return map;
  }

  public static QuoteFieldDescribe buildQuoteField(String name, String label, String quoteField,
      String fieldType) {
    return new QuoteFieldDescribe(buildQuoteFieldMap(name, label, quoteField, fieldType));
  }

  public static Map<String, Object> buildQuoteFieldMap(String name, String label, String quoteField,
      String fieldType) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("api_name", name);
    map.put("define_type", "custom");
    map.put("is_active", true);
    map.put("label", label);
    map.put("quote_field", quoteField);
    map.put("quote_field_type", fieldType);
    map.put("type", "quote");
    return map;
  }
  public static Map<String, Object> buildCurrencyFieldMap(String name, String label) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("api_name", name);
    map.put("define_type", "custom");
    map.put("is_active", true);
    map.put("label", label);
    map.put("length", 18);
    map.put("round_mode", 4);
    map.put("decimal_places", 2);
    map.put("currency_unit", "￥");
    map.put("type", "currency");
    return map;
  }
  public static Map<String, Object> buildFieldOptionMap(String value, String label) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("value", value);
    map.put("label", label);
    return map;
  }
  public static Filter buildFilter(String name,List<String> values,Operator operator, Integer valueType){
    Filter filter = new Filter();
    filter.setValueType(valueType);
    filter.setFieldName(name);
    filter.setOperator(operator);
    filter.setFieldValues(values);
    return filter;
  }
  public static Wheres buildWheres(String connector, List<IFilter > filters){
    Wheres wheres = new Wheres();
    wheres.setConnector(connector);
    wheres.setFilters(filters);
    return wheres;
  }

}
