package com.jstarcraft.core.common.conversion.json;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * JSON工具
 * 
 * @author Birdy
 */
public class JsonUtility {

    /** 类型转换器(基于Jackson) */
    private static final ObjectMapper TYPE_CONVERTER = new ObjectMapper();

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
     * 将任意实例转换为JSON字符串
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
            String message = StringUtility.format("将对象[{}]转换为JSON字符串时异常", instance);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * 将JSON字符串转换为对象
     * 
     * @param json
     * @param type
     * @return
     */
    public static <T> T string2Object(String json, Type type) {
        if (StringUtility.isBlank(json)) {
            return null;
        }
        try {
            return (T) TYPE_CONVERTER.readValue(json, TYPE_FACTORY.constructType(type));
        } catch (Exception exception) {
            String message = StringUtility.format("将JSON字符串[{}]转换为对象时异常", json);
            throw new RuntimeException(message, exception);
        }
    }

    public static JavaType type2Java(Type type) {
        JavaType java = TYPE_FACTORY.constructType(type);
        return java;
    }

    public static Type java2Type(JavaType java) {
        Type type = null;
        if (java.isArrayType()) {
            // 数组类型
            type = java.getRawClass();
        } else if (java.hasGenericTypes()) {
            // 泛型类型
            List<JavaType> javas = java.getBindings().getTypeParameters();
            Type[] generics = new Type[javas.size()];
            int index = 0;
            for (JavaType term : javas) {
                generics[index++] = java2Type(term);
            }
            Class<?> clazz = java.getRawClass();
            type = TypeUtility.parameterize(clazz, generics);
        } else {
            type = java.getRawClass();
        }
        return type;
    }

    /**
     * 对字符串执行JSON加密
     * 
     * @param string
     * @return
     */
    public static final String escapeJson(String string) {
        return StringEscapeUtils.escapeJson(string);
    }

    /**
     * 对字符串执行JSON解密
     * 
     * @param string
     * @return
     */
    public static final String unescapeJson(String string) {
        return StringEscapeUtils.unescapeJson(string);
    }

    public static <T> T convert(Object instance, Type type) {
        // 尝试利用Jackson将指定对象转换为指定类型
        JavaType java = TYPE_FACTORY.constructType(type);
        if (instance instanceof String) {
            try {
                return (T) TYPE_CONVERTER.readValue((String) instance, java);
            } catch (Exception exception) {
                return (T) TYPE_CONVERTER.convertValue(instance, java);
            }
        } else {
            return (T) TYPE_CONVERTER.convertValue(instance, java);
        }
    }

}
