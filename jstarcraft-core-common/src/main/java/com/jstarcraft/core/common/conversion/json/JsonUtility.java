package com.jstarcraft.core.common.conversion.json;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
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

    public static final JsonUtility Instance = new JsonUtility();

    /** 类型转换器(基于Jackson) */
    private static final JsonMapper TYPE_CONVERTER = new JsonMapper();

    /** 类型工厂(基于Jackson) */
    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

    /** 类型转换器(基于Jackson) */
    private static final SimpleModule TYPE_MODULE;

    static {
        TYPE_CONVERTER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        TYPE_CONVERTER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TYPE_MODULE = new JavaTimeModule();

//        TYPE_MODULE.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        TYPE_MODULE.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        TYPE_MODULE.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//        TYPE_MODULE.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        TYPE_MODULE.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        TYPE_MODULE.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

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
        try {
            return TYPE_CONVERTER.writeValueAsString(instance);
        } catch (Exception exception) {
            String message = StringUtility.format("将对象[{}]转换为JSON字符串时异常", instance);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * 将JSON字符串转换为任意实例
     * 
     * @param json
     * @param type
     * @return
     */
    public static <T> T string2Object(String json, Type type) {
        try {
            return (T) TYPE_CONVERTER.readValue(json, TYPE_FACTORY.constructType(type));
        } catch (Exception exception) {
            String message = StringUtility.format("将JSON字符串[{}]转换为对象时异常", json);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * Type转JavaType
     * 
     * <pre>
     * TODO 准备转移到CodecUtility,TypeUtility或者JacksonUtility
     * </pre>
     * 
     * @param type
     * @return
     */
    public static JavaType type2Java(Type type) {
        JavaType java = TYPE_FACTORY.constructType(type);
        return java;
    }

    /**
     * JavaType转Type
     * 
     * <pre>
     * TODO 准备转移到CodecUtility,TypeUtility或者JacksonUtility
     * </pre>
     * 
     * @param type
     * @return
     */
    public static Type java2Type(JavaType java) {
        Type type = null;
        if (java.hasGenericTypes()) {
            if (java.isArrayType()) {
                // 数组类型
                type = java2Type(java.getContentType());
                type = TypeUtility.genericArrayType(type);
            } else {
                // 泛型类型
                List<JavaType> javas = java.getBindings().getTypeParameters();
                int size = javas.size();
                Type[] generics = new Type[size];
                for (int index = 0; index < size; index++) {
                    generics[index] = java2Type(javas.get(index));
                }
                Class<?> clazz = java.getRawClass();
                type = TypeUtility.parameterize(clazz, generics);
            }
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

    /**
     * 格式化JSON
     * 
     * @param json
     * @return
     */
    public static String prettyJson(String json) {
        try {
            Object object = TYPE_CONVERTER.readValue(json, Object.class);
            return TYPE_CONVERTER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception exception) {
            String message = StringUtility.format("将JSON字符串[{}]格式化时异常", json);
            throw new RuntimeException(message, exception);
        }
    }

}
