package com.jstarcraft.core.codec.csv;

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
import com.jstarcraft.core.codec.csv.converter.CsvConverter;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;

/**
 * CSV格式的编解码器
 * 
 * @author Birdy
 */
public class CsvContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvContentCodec.class);

    private CodecDefinition codecDefinition;

    public CsvContentCodec(CodecDefinition definition) {
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
            String message = "CSV解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            CsvReader context = new CsvReader(stream, codecDefinition);
            CsvConverter converter = context.getCsvConverter(Specification.getSpecification(type));
            return converter.readValueFrom(context, type);
        } catch (Exception exception) {
            String message = "CSV解码失败:" + exception.getMessage();
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
            String message = "CSV编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            CsvWriter context = new CsvWriter(stream, codecDefinition);
            CsvConverter converter = context.getCsvConverter(Specification.getSpecification(type));
            converter.writeValueTo(context, type, content);
            context.getOutputStream().flush();
        } catch (Exception exception) {
            String message = "CSV编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
