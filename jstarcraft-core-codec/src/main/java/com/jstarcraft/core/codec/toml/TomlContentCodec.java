package com.jstarcraft.core.codec.toml;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.teesoft.jackson.dataformat.toml.TOMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Toml编解码器
 *
 * @author Yue Zhen Wei
 **/
public class TomlContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomlContentCodec.class);

    @Override
    public Object decode(Type type, byte[] content) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return decode(type, byteArrayInputStream);
        } catch (Exception exception) {
            String message = "Toml解码失败:" + exception.getMessage();
            LOGGER.error(message);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            TOMLMapper mapper = new TOMLMapper();
            return mapper.readValue(stream, TypeUtility.getRawType(type, null));
        } catch (Exception exception) {
            String message = "Toml解码失败:" + exception.getMessage();
            LOGGER.error(message);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            encode(type, content, outputStream);
            return outputStream.toByteArray();
        } catch (Exception exception) {
            String message = "Toml编码失败:" + exception.getMessage();
            exception.printStackTrace();
            LOGGER.error(message);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            TOMLMapper mapper = new TOMLMapper();
            String toml = mapper.writeValueAsString(content);
            mapper.writeValue(stream, toml);
        } catch (Exception exception) {
            String message = "Toml编码失败:" + exception.getMessage();
            exception.printStackTrace();
            LOGGER.error(message);
            throw new CodecException(message, exception);
        }
    }
}
