package com.jstarcraft.core.common.conversion.yaml;

import java.lang.reflect.Type;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jstarcraft.core.utility.StringUtility;

/**
 * YAML工具
 * 
 * @author Birdy
 */
public class YamlUtility {

    /** 类型转换器(基于Jackson) */
    private static final YAMLMapper TYPE_CONVERTER = new YAMLMapper();

    /** 类型工厂(基于Jackson) */
    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

    /** 类型转换器(基于Jackson) */
    private static final SimpleModule TYPE_MODULE;

    static {
        TYPE_CONVERTER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        TYPE_MODULE = new JavaTimeModule();
        TYPE_CONVERTER.registerModule(TYPE_MODULE);
    }

    /**
     * 注册指定类型的序列化器与反序列化器
     * 
     * @param clazz
     * @param deserializer
     * @param serializer
     */
    public static <T> void registerModule(Class<T> clazz, JsonDeserializer<T> deserializer, JsonSerializer<T> serializer) {
        if (deserializer != null) {
            TYPE_MODULE.addDeserializer(clazz, deserializer);
        }
        if (serializer != null) {
            TYPE_MODULE.addSerializer(clazz, serializer);
        }
    }

    /**
     * 将任意实例转换为YAML字符串
     * 
     * @param instance
     * @return
     */
    public static String object2String(Object instance) {
        if (instance == null) {
            return null;
        }
        try {
            return TYPE_CONVERTER.writeValueAsString(instance);
        } catch (Exception exception) {
            String message = StringUtility.format("将对象[{}]转换为YAML字符串时异常", instance);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * 将YAML字符串转换为对象
     * 
     * @param yaml
     * @param type
     * @return
     */
    public static <T> T string2Object(String yaml, Type type) {
        if (StringUtility.isBlank(yaml)) {
            return null;
        }
        try {
            return (T) TYPE_CONVERTER.readValue(yaml, TYPE_FACTORY.constructType(type));
        } catch (Exception exception) {
            String message = StringUtility.format("将YAML字符串[{}]转换为对象时异常", yaml);
            throw new RuntimeException(message, exception);
        }
    }

}
