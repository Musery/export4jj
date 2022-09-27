package com.musery.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonUtils.
 *
 * @author 范俊翔
 * @date 2022/3/24 16:59
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
  public static final String DATE_TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    // 忽略null
    OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
    // 添加 格式化 LocalDateTime 为 yyyy-MM-dd HH:mm:ss 字符串
    OBJECT_MAPPER.registerModule(
        new JavaTimeModule()
            .addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING)))
            .addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING))));
    // 添加 jdk8 支持
    OBJECT_MAPPER.registerModule(new Jdk8Module());
    OBJECT_MAPPER.registerModule(new ParameterNamesModule());
    OBJECT_MAPPER.registerModule(new SimpleModule());
    // 开启 key 排序
    OBJECT_MAPPER.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    // 忽略空Bean转json的错误
    OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    // 忽略未知属性，防止json字符串中存在，java对象中不存在对应属性的情况出现错误
    OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  /**
   * 对象 => json字符串(如果是空对象, 返回 null).
   *
   * @param obj 源对象
   */
  public static String toJson(Object obj) {
    try {
      return OBJECT_MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * json字符串 => 对象(如果是 空字符串, 返回 null).
   *
   * @param json 源json串
   * @param clazz 对象类
   * @param <T> 泛型
   */
  public static <T> T parse(String json, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <T> T parse(byte[] jsonBytes, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(jsonBytes, clazz);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * json字符串 => 对象(如果是 空字符串, 返回 null).
   *
   * @param json 源json串
   * @param type 对象类型
   * @param <T> 泛型
   */
  public static <T> T parse(String json, TypeReference<T> type) {
    try {
      return OBJECT_MAPPER.readValue(json, type);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /** 将字符串转list对象(如果是 空字符串, 返回 emptyList). */
  public static <T> List<T> parseToList(String json, Class<T> clazz) {
    try {
      JavaType t = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
      return OBJECT_MAPPER.readValue(json, t);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static Map<String, String> parseToMap(String json) {

    return parse(json, new TypeReference<Map<String, String>>() {});
  }

  public static boolean isNull(JsonNode node) {
    return Objects.isNull(node) || node.isNull();
  }

  /**
   * 获取指定的属性值.
   *
   * @param data json 对象
   * @param fieldName 属性名称
   * @param defaultValue 为空时的默认值
   * @author fanjx
   * @date 2021/3/12 5:36 下午
   */
  public static String getText(JsonNode data, String fieldName, String defaultValue) {

    if (isNull(data)) {
      return defaultValue;
    }

    JsonNode fieldNode = data.get(fieldName);
    if (isNull(fieldNode)) {
      return defaultValue;
    }

    return fieldNode.asText(defaultValue);
  }

  public static Long getLong(JsonNode data, String fieldName, Long defaultValue) {

    if (isNull(data)) {
      return defaultValue;
    }

    JsonNode fieldNode = data.get(fieldName);
    if (isNull(fieldNode)) {
      return defaultValue;
    }

    return fieldNode.asLong(defaultValue);
  }

  public static Long getLong(JsonNode data, String fieldName) {
    return getLong(data, fieldName, 0L);
  }

  public static Integer getInt(JsonNode data, String fieldName, Integer defaultValue) {

    if (isNull(data)) {
      return defaultValue;
    }

    JsonNode fieldNode = data.get(fieldName);
    if (isNull(fieldNode)) {
      return defaultValue;
    }

    return fieldNode.asInt(defaultValue);
  }

  public static Integer getInt(JsonNode data, String fieldName) {
    return getInt(data, fieldName, 0);
  }

  public static JsonNode parseToJsonNode(String json) {
    return parse(json, JsonNode.class);
  }

  public static ArrayNode parseToJsonArray(String json) {
    return parse(json, ArrayNode.class);
  }

  public static ObjectNode parseToObjectNode(String json) {
    return parse(json, ObjectNode.class);
  }

  public static ObjectNode parseToObjectNode(byte[] jsonBytes) {
    return parse(jsonBytes, ObjectNode.class);
  }

  public static ObjectMapper singletonMapper() {
    return OBJECT_MAPPER;
  }

  public static ObjectNode createObjectNode() {
    return OBJECT_MAPPER.createObjectNode();
  }
}
