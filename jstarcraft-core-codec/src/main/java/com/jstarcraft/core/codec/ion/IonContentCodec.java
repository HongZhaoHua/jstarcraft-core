package com.jstarcraft.core.codec.ion;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.ion.IonObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.jackson.TypeJacksonDeserializer;
import com.jstarcraft.core.codec.jackson.TypeJacksonSerializer;
import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.reflection.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Ion编解码器
 *
 * @author Yue Zhen Wei
 **/
public class IonContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(IonContentCodec.class);

    private ObjectMapper typeConverter;

    public IonContentCodec() {
        typeConverter = new IonObjectMapper();
        // 修改为基于JsonSerializer和JsonDeserializer
        typeConverter.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        typeConverter.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        typeConverter.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        typeConverter.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        typeConverter.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        JavaTimeModule module = new JavaTimeModule();
        typeConverter.registerModule(module);
        module.addDeserializer(Type.class, new TypeJacksonDeserializer());
        module.addSerializer(Type.class, new TypeJacksonSerializer());
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try {
            Specification specification = Specification.getSpecification(type);
            if (Specification.TYPE == specification) {
                return typeConverter.readValue(content, Type.class);
            } else {
                return typeConverter.readValue(content, JsonUtility.type2Java(type));
            }
        } catch (Exception exception) {
            String message = "Ion解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            Specification specification = Specification.getSpecification(type);
            if (Specification.TYPE == specification) {
                return typeConverter.readValue(stream, Type.class);
            } else {
                return typeConverter.readValue(stream, JsonUtility.type2Java(type));
            }
        } catch (Exception exception) {
            String message = "Ion解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        try {
            return typeConverter.writeValueAsBytes(content);
        } catch (Exception exception) {
            String message = "Ion编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            typeConverter.writeValue(stream, content);
        } catch (Exception exception) {
            String message = "Ion编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
