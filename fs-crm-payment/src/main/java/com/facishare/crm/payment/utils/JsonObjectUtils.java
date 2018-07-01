package com.facishare.crm.payment.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configuration.Defaults;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base on JsonPath and Jackson to modify map based object.
 *
 * Using Jackson to serialize and deserialize object, and using JsonPath to modify serialized json string.
 * For more information about JsonPath, visit <a href="https://github.com/json-path/JsonPath">https://github.com/json-path/JsonPath</a>.
 *
 * PS: Method in this class is not guarantee modify object safely, field type changed may cause object deserialize failed.
 */
public class JsonObjectUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectUtils.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    Configuration.setDefaults(new Defaults() {
      private final JsonProvider jsonProvider = new JacksonJsonProvider();
      private final MappingProvider mappingProvider = new JacksonMappingProvider();

      @Override
      public JsonProvider jsonProvider() {
        return jsonProvider;
      }

      @Override
      public Set<Option> options() {
        return ImmutableSet.of(Option.ALWAYS_RETURN_LIST);
      }

      @Override
      public MappingProvider mappingProvider() {
        return mappingProvider;
      }
    });
  }

  private static DocumentContext getDocument(Object source) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      return JsonPath.parse(MAPPER.writeValueAsString(source));
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return null;
    } finally {
      LOGGER.debug("Json string parse in {} ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
  }

  private static <T> T parseOrDefault(String json, Class<T> clazz, T defaultValue) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      return MAPPER.readValue(json, clazz);
    } catch (IOException ex) {
      LOGGER.warn(ex.getMessage(), ex);
      return defaultValue;
    } finally {
      LOGGER.debug("Json object parse in {} ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
  }

  /**
   * Append entry object on certain path.
   * Do add for array, putAll for Map, and nothing for others.
   */
  public static <T> T append(T source, Class<T> clazz, String path, Object entry) {
    return append(source, clazz, path, entry, false);
  }

  /**
   * Append entry object on certain path.
   * Do add / addAll( if merge is true ) for array, putAll for Map, and nothing for others.
   */
  @SuppressWarnings("unchecked")
  public static <T> T append(T source, Class<T> clazz, String path, Object entry, boolean merge) {
    DocumentContext document = getDocument(source);
    if (null == document) {
      return source;
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    List<Object> matches = document.read(path);
    if (CollectionUtils.isEmpty(matches)) {
      return source;
    }
    Object match = matches.get(0);
    if (match instanceof List) {
      if (merge && entry instanceof List) {
        ((List) match).addAll((List) entry);
      } else {
        ((List) match).add(entry);
      }
    } else if (match instanceof Map) {
      ((Map) match).putAll((Map) entry);
    } else {
      return source;
    }
    DocumentContext updatedDocument = document.set(path, match);
    LOGGER.debug("Json string modified in {} ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return parseOrDefault(updatedDocument.jsonString(), clazz, source);
  }

  /**
   * Update object to entry on certain path.
   */
  public static <T> T update(T source, Class<T> clazz, String path, Object entry) {
    DocumentContext document = getDocument(source);
    if (null == document) {
      return source;
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    DocumentContext updatedDocument = document.set(path, entry);
    LOGGER.debug("Json string modified in {} ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return parseOrDefault(updatedDocument.jsonString(), clazz, source);
  }

  /**
   * Remove object on certain path.
   * Do nothing if the path could not be parsed.
   */
  public static <T> T remove(T source, Class<T> clazz, String path) {
    DocumentContext document = getDocument(source);
    if (null == document) {
      return source;
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    DocumentContext updatedDocument = document.delete(path);
    LOGGER.debug("Json string modified in {} ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return parseOrDefault(updatedDocument.jsonString(), clazz, source);
  }

  public static <T> T get(Object source, Class<T> clazz, String path) {
    DocumentContext document = getDocument(source);
    if (null == document) {
      return null;
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    List<Object> matches = document.read(path);
    if (CollectionUtils.isEmpty(matches)) {
      return null;
    }
    Object match = matches.get(0);
    LOGGER.debug("Json string founded in {} ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return parseOrDefault(JSONObject.toJSONString(match), clazz, null);
  }
}
