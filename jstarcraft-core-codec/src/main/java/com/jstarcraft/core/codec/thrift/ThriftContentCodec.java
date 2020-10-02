package com.jstarcraft.core.codec.thrift;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.function.Function;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.thrift.converter.ThriftContext;
import com.jstarcraft.core.codec.thrift.converter.ThriftConverter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Thrift格式的编解码器
 * 
 * @author Huang Hong Fei
 *
 */
public class ThriftContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftContentCodec.class);

    private CodecDefinition codecDefinition;

    private Function<TTransport, TProtocol> protocolFactory;

    public ThriftContentCodec(CodecDefinition codecDefinition, Function<TTransport, TProtocol> protocolFactory) {
        this.codecDefinition = codecDefinition;
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return decode(type, byteArrayInputStream);
        } catch (Exception exception) {
            String message = "Thrift解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try (TIOStreamTransport transport = new TIOStreamTransport(stream)) {
            if (stream.available() == 0) {
                return null;
            }
            ThriftContext context = new ThriftContext(codecDefinition, protocolFactory.apply(transport));
            ThriftConverter converter = context.getThriftConverter(Specification.getSpecification(type));
            ClassDefinition classDefinition = codecDefinition.getClassDefinition(TypeUtility.getRawType(type, null));
            return converter.readValueFrom(context, type, classDefinition);
        } catch (Exception exception) {
            String message = "Thrift解码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        if (content == null) {
            return new byte[] {};
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            encode(type, content, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            String message = "Thrift编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try (TIOStreamTransport transport = new TIOStreamTransport(stream)) {
            if (content == null) {
                return;
            }
            ThriftContext context = new ThriftContext(codecDefinition, protocolFactory.apply(transport));
            ThriftConverter converter = context.getThriftConverter(Specification.getSpecification(type));
            ClassDefinition classDefinition = codecDefinition.getClassDefinition(TypeUtility.getRawType(type, null));
            converter.writeValueTo(context, type, classDefinition, content);
        } catch (Exception exception) {
            String message = "Thrift编码失败:" + exception.getMessage();
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
