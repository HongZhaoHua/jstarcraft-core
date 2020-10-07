package com.jstarcraft.core.codec.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.avro.converter.AvroConverter;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;

/**
 *
 * @author: MnZzV
 **/
public class AvroContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroContentCodec.class);

    private CodecDefinition codecDefinition;

    public AvroContentCodec(CodecDefinition codecDefinition) {
        this.codecDefinition = codecDefinition;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        if (content.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return decode(type, byteArrayInputStream);
        } catch (Exception exception) {
            String message = "Avro解码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            int available = stream.available();
            if (available == 0) {
                return null;
            }
            final AvroReader avroReader = new AvroReader(stream, this.codecDefinition);
            final AvroConverter<?> avroConverter = avroReader.getAvroConverter(Specification.getSpecification(type));
            return avroConverter.readValueFrom(avroReader, type);
        } catch (Exception exception) {
            String message = "Avro解码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        if (content == null) {
            return new byte[] {};
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            encode(type, content, outputStream);
            return outputStream.toByteArray();
        } catch (Exception exception) {
            String message = "Avro编码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream outputStream) {
        if (content == null) {
            return;
        }
        try {
            final AvroWriter avroContext = new AvroWriter(outputStream, this.codecDefinition);
            final AvroConverter avroConverter = avroContext.getAvroConverter(Specification.getSpecification(type));
            avroConverter.writeValueTo(avroContext, type, content);
        } catch (Exception exception) {
            String message = "Avro编码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }
    }
}
