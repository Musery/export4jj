package com.musery.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON-对象转换工具类
 *
 * @author jonathan
 */
@Slf4j
public class JacksonUtils {

  private static ObjectMapper nilOM = new ObjectMapper();

  /** nilOM忽略空字段序列化 */
  static {
    nilOM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // 增加LocalDateTime序列化配置
    nilOM.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    nilOM.registerModule(new JavaTimeModule());
    nilOM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    nilOM.setTimeZone(TimeZone.getDefault());
  }

  /** 获取对应序列化用ObjectMapper */
  @JsonFormat
  public static ObjectMapper getOM() {
    return nilOM;
  }

  public static Map toMap(Object obj) {
    if (null == obj) {
      return null;
    }
    if (obj instanceof Map) {
      return (Map) obj;
    }
    if (obj instanceof String) {
      return toObject((String) obj, Map.class);
    }
    return getOM().convertValue(obj, Map.class);
  }

  /** 对象转化成JSON格式字符串 */
  public static String toString(Object obj) {
    if (null == obj) {
      return null;
    }
    if (obj instanceof String) {
      return (String) obj;
    }
    try {
      ObjectMapper om = getOM();
      return om.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error("实体类[{}]转化失败", obj.getClass(), e);
      throw new RuntimeException(e);
    }
  }

  /** 将JSON格式数据转化成对象 */
  public static <T> T toObject(String json, Class<T> valueType) {
    try {
      if (null == json) {
        return null;
      }
      if (valueType.equals(String.class)) {
        return (T) json;
      }
      ObjectMapper om = getOM();
      return om.readValue(json, valueType);
    } catch (IOException e) {
      log.error("数据转化失败:[{}]", json, e);
      throw new RuntimeException(e);
    }
  }

  /** JSON格式字符串转化对象(深度转化) */
  public static <T> T deepToObject(String json, TypeReference<T> valueTypeRef) {
    try {
      ObjectMapper om = getOM();
      return om.readValue(json, valueTypeRef);
    } catch (IOException e) {
      log.error("数据转化失败:[{}]", json, e);
      throw new RuntimeException(e);
    }
  }

  public static <T> T deepToObject(InputStream stream, TypeReference<T> valueTypeRef) {
    try {
      ObjectMapper om = getOM();
      return om.readValue(stream, valueTypeRef);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
