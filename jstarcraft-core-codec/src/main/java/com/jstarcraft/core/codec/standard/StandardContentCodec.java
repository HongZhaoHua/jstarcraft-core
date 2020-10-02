package com.jstarcraft.core.codec.standard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.standard.converter.StandardConverter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Standard格式编解码器
 * 
 * @author Birdy
 */
public class StandardContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardContentCodec.class);

    private CodecDefinition codecDefinition;

    public StandardContentCodec(CodecDefinition definition) {
        this.codecDefinition = definition;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content); DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            return decode(type, dataInputStream);
        } catch (Exception exception) {
            String message = "Standard解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            StandardReader context = new StandardReader(stream, codecDefinition);
            StandardConverter converter = context.getStandardConverter(Specification.getSpecification(type));
            ClassDefinition classDefinition = codecDefinition.getClassDefinition(TypeUtility.getRawType(type, null));
            return converter.readValueFrom(context, type, classDefinition);
        } catch (Exception exception) {
            String message = "Standard解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            encode(type, content, dataOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            String message = "Standard编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            StandardWriter context = new StandardWriter(stream, codecDefinition);
            StandardConverter converter = context.getStandardConverter(Specification.getSpecification(type));
            ClassDefinition classDefinition = codecDefinition.getClassDefinition(TypeUtility.getRawType(type, null));
            converter.writeValueTo(context, type, classDefinition, content);
        } catch (Exception exception) {
            String message = "Standard编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
