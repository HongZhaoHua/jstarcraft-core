package com.jstarcraft.core.codec.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.ExtSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.hessian.converter.InstantDeserializer;
import com.jstarcraft.core.codec.hessian.converter.InstantSerializer;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Hessian格式的编解码器
 * 
 * @author Birdy
 */
public class HessianContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(HessianContentCodec.class);

    private static final SerializerFactory hessianFactory = new SerializerFactory();
    {
        ExtSerializerFactory factory = new ExtSerializerFactory();
        factory.addDeserializer(Instant.class, new InstantDeserializer());
        factory.addSerializer(Instant.class, new InstantSerializer());
        hessianFactory.setAllowNonSerializable(true);
        hessianFactory.addFactory(factory);
    }

    private CodecDefinition codecDefinition;

    public HessianContentCodec(CodecDefinition definition) {
        this.codecDefinition = definition;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        if (content.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content); DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            return decode(type, dataInputStream);
        } catch (Exception exception) {
            String message = "Hessian解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            Hessian2Input input = new Hessian2Input();
            input.setSerializerFactory(hessianFactory);
            input.init(stream);
            Object content = input.readObject(TypeUtility.getRawType(type, null));
            input.close();
            return content;
        } catch (Exception exception) {
            String message = "Hessian解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        if (content == null) {
            return new byte[] {};
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            encode(type, content, dataOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            String message = "Hessian编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            Hessian2Output output = new Hessian2Output();
            output.setSerializerFactory(hessianFactory);
            output.init(stream);
            output.writeObject(content);
            output.close();
        } catch (Exception exception) {
            String message = "Hessian编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
