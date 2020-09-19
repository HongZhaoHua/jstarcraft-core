package com.jstarcraft.core.codec.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.ExtSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.hessian.converter.DurationDeserializer;
import com.jstarcraft.core.codec.hessian.converter.DurationSerializer;
import com.jstarcraft.core.codec.hessian.converter.InstantDeserializer;
import com.jstarcraft.core.codec.hessian.converter.InstantSerializer;
import com.jstarcraft.core.codec.hessian.converter.LocalDateDeserializer;
import com.jstarcraft.core.codec.hessian.converter.LocalDateSerializer;
import com.jstarcraft.core.codec.hessian.converter.LocalDateTimeDeserializer;
import com.jstarcraft.core.codec.hessian.converter.LocalDateTimeSerializer;
import com.jstarcraft.core.codec.hessian.converter.LocalTimeDeserializer;
import com.jstarcraft.core.codec.hessian.converter.LocalTimeSerializer;
import com.jstarcraft.core.codec.hessian.converter.MonthDayDeserializer;
import com.jstarcraft.core.codec.hessian.converter.MonthDaySerializer;
import com.jstarcraft.core.codec.hessian.converter.OffsetDateTimeDeserializer;
import com.jstarcraft.core.codec.hessian.converter.OffsetDateTimeSerializer;
import com.jstarcraft.core.codec.hessian.converter.OffsetTimeDeserializer;
import com.jstarcraft.core.codec.hessian.converter.OffsetTimeSerializer;
import com.jstarcraft.core.codec.hessian.converter.PeriodDeserializer;
import com.jstarcraft.core.codec.hessian.converter.PeriodSerializer;
import com.jstarcraft.core.codec.hessian.converter.YearMonthDeserializer;
import com.jstarcraft.core.codec.hessian.converter.YearMonthSerializer;
import com.jstarcraft.core.codec.hessian.converter.ZoneOffsetDeserializer;
import com.jstarcraft.core.codec.hessian.converter.ZoneOffsetSerializer;
import com.jstarcraft.core.codec.hessian.converter.ZonedDateTimeDeserializer;
import com.jstarcraft.core.codec.hessian.converter.ZonedDateTimeSerializer;
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
        factory.addDeserializer(Duration.class, new DurationDeserializer());
        factory.addSerializer(Duration.class, new DurationSerializer());
        factory.addDeserializer(Instant.class, new InstantDeserializer());
        factory.addSerializer(Instant.class, new InstantSerializer());
        factory.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        factory.addSerializer(LocalDate.class, new LocalDateSerializer());
        factory.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        factory.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        factory.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        factory.addSerializer(LocalTime.class, new LocalTimeSerializer());
        factory.addDeserializer(MonthDay.class, new MonthDayDeserializer());
        factory.addSerializer(MonthDay.class, new MonthDaySerializer());
        factory.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());
        factory.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        factory.addDeserializer(OffsetTime.class, new OffsetTimeDeserializer());
        factory.addSerializer(OffsetTime.class, new OffsetTimeSerializer());
        factory.addDeserializer(Period.class, new PeriodDeserializer());
        factory.addSerializer(Period.class, new PeriodSerializer());
        factory.addDeserializer(YearMonth.class, new YearMonthDeserializer());
        factory.addSerializer(YearMonth.class, new YearMonthSerializer());
        factory.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        factory.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        factory.addDeserializer(ZoneOffset.class, new ZoneOffsetDeserializer());
        factory.addSerializer(ZoneOffset.class, new ZoneOffsetSerializer());
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
