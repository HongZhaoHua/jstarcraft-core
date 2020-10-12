package com.jstarcraft.core.codec.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

/**
 * XML格式编解码器
 * 
 * @author Birdy
 */
public class XmlContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlContentCodec.class);

    private final CodecDefinition codecDefinition;
    /** 类型转换器(基于XStream) */
    private final XStream typeConverter;

    public XmlContentCodec(CodecDefinition definition) {
        this(definition, new XStream());
    }

    public XmlContentCodec(CodecDefinition definition, HierarchicalStreamDriver driver) {
        this(definition, new XStream(driver));
    }

    public XmlContentCodec(CodecDefinition definition, XStream typeConverter) {
        this.codecDefinition = definition;
        this.typeConverter = typeConverter;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return decode(type, byteArrayInputStream);
        } catch (Exception exception) {
            String message = "XML解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            return typeConverter.fromXML(stream);
        } catch (Exception exception) {
            String message = "XML解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            encode(type, content, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            String message = "XML编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            typeConverter.toXML(content, stream);
        } catch (Exception exception) {
            String message = "XML编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
