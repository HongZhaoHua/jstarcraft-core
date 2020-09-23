package com.jstarcraft.core.codec.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * JSON格式编解码器
 * 
 * @author Birdy
 */
public class JsonContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonContentCodec.class);

    private final ThreadLocal<Type> currentTypes = new ThreadLocal<>();

    private final CodecDefinition codecDefinition;
    /** 类型转换器(基于Jackson) */
    private final JsonMapper typeConverter = new JsonMapper();

    public JsonContentCodec(CodecDefinition definition) {
        this.codecDefinition = definition;
        // 修改为基于JsonSerializer和JsonDeserializer
        typeConverter.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        typeConverter.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        typeConverter.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        typeConverter.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        typeConverter.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        JsonDeserializer<Type> typeDeserializer = new JsonDeserializer<Type>() {

            private Type readValueFrom(Iterator<JsonNode> iterator) throws IOException {
                Integer code = iterator.next().asInt();
                ClassDefinition definition = codecDefinition.getClassDefinition(code);
                if (definition.getType() == Class.class) {
                    code = iterator.next().asInt();
                    definition = codecDefinition.getClassDefinition(code);
                    return definition.getType();
                } else if (definition.getType() == GenericArrayType.class) {
                    Type type = currentTypes.get();
                    if (type == Class.class) {
                        type = readValueFrom(iterator);
                        Class<?> clazz = Class.class.cast(type);
                        return Array.newInstance(clazz, 0).getClass();
                    } else {
                        type = readValueFrom(iterator);
                        return TypeUtility.genericArrayType(type);
                    }
                } else if (definition.getType() == ParameterizedType.class) {
                    code = iterator.next().asInt();
                    definition = codecDefinition.getClassDefinition(code);
                    Integer length = iterator.next().asInt();
                    Type[] types = new Type[length];
                    for (int index = 0; index < length; index++) {
                        types[index] = readValueFrom(iterator);
                    }
                    return TypeUtility.parameterize(definition.getType(), types);
                } else {
                    throw new CodecConvertionException();
                }
            }

            public Type deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
                ObjectCodec objectCodec = parser.getCodec();
                JsonNode node = objectCodec.readTree(parser);
                Type type = readValueFrom(node.iterator());
                return type;
            }

        };

        JsonSerializer<Type> typeSerializer = new JsonSerializer<Type>() {

            private void writeValueTo(JsonGenerator out, Type value) throws IOException {
                if (TypeUtility.isInstance(value, Class.class)) {
                    Class<?> clazz = TypeUtility.getRawType(value, null);
                    if (clazz.isArray()) {
                        ClassDefinition definition = codecDefinition.getClassDefinition(GenericArrayType.class);
                        out.writeNumber(definition.getCode());
                        value = TypeUtility.getArrayComponentType(value);
                        writeValueTo(out, value);
                    } else {
                        ClassDefinition definition = codecDefinition.getClassDefinition(Class.class);
                        out.writeNumber(definition.getCode());
                        definition = codecDefinition.getClassDefinition(clazz);
                        out.writeNumber(definition.getCode());
                    }
                } else if (TypeUtility.isInstance(value, GenericArrayType.class)) {
                    ClassDefinition definition = codecDefinition.getClassDefinition(GenericArrayType.class);
                    out.writeNumber(definition.getCode());
                    value = TypeUtility.getArrayComponentType(value);
                    writeValueTo(out, value);
                } else if (TypeUtility.isInstance(value, ParameterizedType.class)) {
                    ClassDefinition definition = codecDefinition.getClassDefinition(ParameterizedType.class);
                    out.writeNumber(definition.getCode());
                    Class<?> clazz = TypeUtility.getRawType(value, null);
                    definition = codecDefinition.getClassDefinition(clazz);
                    out.writeNumber(definition.getCode());
                    ParameterizedType parameterizedType = (ParameterizedType) value;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    out.writeNumber(types.length);
                    for (int index = 0; index < types.length; index++) {
                        writeValueTo(out, types[index]);
                    }
                } else {
                    throw new CodecConvertionException();
                }
            }

            public void serialize(Type value, JsonGenerator generator, SerializerProvider serializers) throws IOException, JsonProcessingException {
                generator.writeStartArray();
                writeValueTo(generator, value);
                generator.writeEndArray();
            }

        };
        JavaTimeModule module = new JavaTimeModule();
        typeConverter.registerModule(module);
        module.addDeserializer(Type.class, typeDeserializer);
        module.addSerializer(Type.class, typeSerializer);
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try {
            if (content.length == 0) {
                return null;
            }
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                currentTypes.set(type);
                Type value = typeConverter.readValue(content, Type.class);
                currentTypes.remove();
                return value;
            } else {
                return typeConverter.readValue(content, JsonUtility.type2Java(type));
            }
        } catch (Exception exception) {
            String message = "JSON解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                currentTypes.set(type);
                Type value = typeConverter.readValue(stream, Type.class);
                currentTypes.remove();
                return value;
            } else {
                return typeConverter.readValue(stream, JsonUtility.type2Java(type));
            }
        } catch (Exception exception) {
            String message = "JSON解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        try {
            if (content == null) {
                return new byte[] {};
            }
            currentTypes.set(type);
            byte[] value = typeConverter.writeValueAsBytes(content);
            currentTypes.remove();
            return value;
        } catch (Exception exception) {
            String message = "JSON编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            currentTypes.set(type);
            typeConverter.writeValue(stream, content);
            currentTypes.remove();
        } catch (Exception exception) {
            String message = "JSON编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
