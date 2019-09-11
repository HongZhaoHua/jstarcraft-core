package com.jstarcraft.core.codec.protocolbufferx;

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
import com.jstarcraft.core.codec.protocolbufferx.converter.ProtocolConverter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Prorocol格式编解码器
 * 
 * @author Birdy
 */
public class ProtocolContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolContentCodec.class);

    private CodecDefinition codecDefinition;

    public ProtocolContentCodec(CodecDefinition definition) {
        this.codecDefinition = definition;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content); DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            return decode(type, dataInputStream);
        } catch (Exception exception) {
            String message = "Protocol解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            ProtocolReader context = new ProtocolReader(stream, codecDefinition);
            ProtocolConverter converter = context.getProtocolConverter(Specification.getSpecification(type));
            ClassDefinition classDefinition = codecDefinition.getClassDefinition(TypeUtility.getRawType(type, null));
            return converter.readValueFrom(context, type, classDefinition);
        } catch (Exception exception) {
            String message = "Protocol解码失败:" + exception.getMessage();
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
            String message = "Protocol编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            ProtocolWriter context = new ProtocolWriter(stream, codecDefinition);
            ProtocolConverter converter = context.getProtocolConverter(Specification.getSpecification(type));
            ClassDefinition classDefinition = codecDefinition.getClassDefinition(TypeUtility.getRawType(type, null));
            converter.writeValueTo(context, type, classDefinition, content);
        } catch (Exception exception) {
            String message = "Protocol编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
