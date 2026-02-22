package com.chen.HospitalSelection.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 * 提供JSON与对象之间的转换方法
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("对象转JSON失败", e);
            return null;
        }
    }

    /**
     * JSON字符串转对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * JSON字符串转对象（支持泛型）
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * JSON字符串转List
     *
     * @param json JSON字符串
     * @param clazz 元素类型
     * @param <T>  泛型
     * @return List对象
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            logger.error("JSON转List失败", e);
            return null;
        }
    }

    /**
     * JSON字符串转Map
     *
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            logger.error("JSON转Map失败", e);
            return null;
        }
    }

    /**
     * 对象转Map
     *
     * @param obj 对象
     * @return Map对象
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            String json = toJson(obj);
            return fromJsonToMap(json);
        } catch (Exception e) {
            logger.error("对象转Map失败", e);
            return null;
        }
    }

    /**
     * Map转对象
     *
     * @param map  Map对象
     * @param clazz 目标类型
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            String json = toJson(map);
            return fromJson(json, clazz);
        } catch (Exception e) {
            logger.error("Map转对象失败", e);
            return null;
        }
    }

    /**
     * 美化JSON字符串
     *
     * @param json JSON字符串
     * @return 美化后的JSON字符串
     */
    public static String prettyPrint(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("美化JSON失败", e);
            return json;
        }
    }

    /**
     * 判断字符串是否为有效的JSON
     *
     * @param json 字符串
     * @return true-有效，false-无效
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
