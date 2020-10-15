package com.jstarcraft.core.codec.cbor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Cbor 编解码器
 *
 * @author Yue Zhen Wei
 **/
public class CborContentCodec implements ContentCodec {

    private ObjectMapper mapper;

    public CborContentCodec() {
        mapper = new ObjectMapper(new CBORFactory());

        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try {
            Class<?> clazz = TypeUtility.getRawType(type, null);
            Object value;
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                String typeStr = mapper.readValue(content, String.class);
                value = TypeUtility.string2Type(typeStr);
            } else {
                value = mapper.readValue(content, clazz);
            }
            return value;
        } catch (Exception exception) {
            String message = "Cbor解码失败:" + exception.getMessage();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            Class<?> clazz = TypeUtility.getRawType(type, null);
            Object value;
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                String typeStr = mapper.readValue(stream, String.class);
                value = TypeUtility.string2Type(typeStr);
            } else {
                value = mapper.readValue(stream, clazz);
            }
            return value;
        } catch (Exception exception) {
            String message = "Cbor解码失败:" + exception.getMessage();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        try {
            return mapper.writeValueAsBytes(content);
        } catch (Exception exception) {
            String message = "Cbro编码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                content = TypeUtility.type2String((Type) content);
            }

            mapper.writeValue(stream, content);
        } catch (Exception exception) {
            String message = "Cbro编码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }

    }
}
