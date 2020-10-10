package com.jstarcraft.core.codec.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.function.BiFunction;

import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions.TimestampMillisConversion;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.avro.conversion.AtomicBooleanConversion;
import com.jstarcraft.core.codec.avro.conversion.AtomicIntegerConversion;
import com.jstarcraft.core.codec.avro.conversion.AtomicLongConversion;
import com.jstarcraft.core.codec.avro.conversion.DateConversion;
import com.jstarcraft.core.codec.avro.conversion.TypeConversion;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.CodecDefinition;

/**
 * Avro编解码器
 * 
 * @author Yue Zhen Wei
 *
 */
public class AvroContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroContentCodec.class);

    private CodecDefinition codecDefinition;

    private BiFunction<Schema, InputStream, Decoder> decoderFactory;

    private BiFunction<Schema, OutputStream, Encoder> encoderFactory;

    private AvroData utility;

    public AvroContentCodec(CodecDefinition codecDefinition, BiFunction<Schema, InputStream, Decoder> decoderFactory, BiFunction<Schema, OutputStream, Encoder> encoderFactory) {
        this.codecDefinition = codecDefinition;
        this.decoderFactory = decoderFactory;
        this.encoderFactory = encoderFactory;
        this.utility = new AvroData();
        this.utility.addLogicalTypeConversion(new AtomicBooleanConversion());

        this.utility.addLogicalTypeConversion(new AtomicIntegerConversion());
        this.utility.addLogicalTypeConversion(new AtomicLongConversion());

        this.utility.addLogicalTypeConversion(new DateConversion());
        this.utility.addLogicalTypeConversion(new TimestampMillisConversion());

        this.utility.addLogicalTypeConversion(new TypeConversion(Type.class, "type"));
        this.utility.addLogicalTypeConversion(new TypeConversion(Class.class, "class"));
        this.utility.addLogicalTypeConversion(new TypeConversion(GenericArrayType.class, "generic-array-type"));
        this.utility.addLogicalTypeConversion(new TypeConversion(ParameterizedType.class, "parameterized-type"));
        this.utility.addLogicalTypeConversion(new TypeConversion(TypeVariable.class, "type-variable"));
        this.utility.addLogicalTypeConversion(new TypeConversion(WildcardType.class, "wildcard-type"));
    }

    @Override
    public Object decode(Type type, byte[] content) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return decode(type, byteArrayInputStream);
        } catch (Exception exception) {
            String message = "Avro解码失败:" + exception.getMessage();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try {
            Schema schema = utility.getSchema(type);
            schema = utility.makeNullable(schema);
            DatumReader reader = new AvroDatumReader(schema, utility);
            Decoder decoder = decoderFactory.apply(schema, stream);
            return reader.read(null, decoder);
        } catch (Exception exception) {
            String message = "Avro解码失败:" + exception.getMessage();
            exception.printStackTrace();
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
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
    public void encode(Type type, Object content, OutputStream stream) {
        try {
            Schema schema = utility.getSchema(type);
            schema = utility.makeNullable(schema);
            DatumWriter writer = new AvroDatumWriter(schema, utility);
            Encoder encoder = encoderFactory.apply(schema, stream);
            writer.write(content, encoder);
            encoder.flush();
        } catch (Exception exception) {
            String message = "Avro编码失败:" + exception.getMessage();
            throw new CodecException(message, exception);
        }
    }
}
