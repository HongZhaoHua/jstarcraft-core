package com.jstarcraft.core.codec.avro.converter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Avro转换器
 * 
 * @author Yue Zhen Wei
 *
 * @param <T>
 */
public abstract class AvroConverter<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract T readValue(AvroReader avroReader, Object input, Type type) throws Exception;

    /**
     * 从指定上下文读取内容
     *
     * @param avroReader
     * @param type
     * @return
     * @throws IOException
     */
    public final T readValueFrom(AvroReader avroReader, Type type) throws Exception {
        GenericDatumReader userDatumReader = new GenericDatumReader<>(getSchema(type));
        BinaryDecoder binaryEncoder = DecoderFactory.get().directBinaryDecoder(avroReader.getInputStream(), null);
        Object read = userDatumReader.read(new Object(), binaryEncoder);
        return readValue(avroReader, read, type);
    }

    /**
     * 将指定内容写到上下文
     *
     * @param writer
     * @param type
     * @param value
     * @throws IOException
     */
    public final void writeValueTo(AvroWriter writer, Type type, T value) throws Exception {
        Schema schema = this.getSchema(type);
        Object writeValue = writeValue(writer, value, type);
        SpecificDatumWriter<Object> specificDatumWriter = new SpecificDatumWriter<>(schema);
        BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(writer.getOutputStream(), null);
        specificDatumWriter.write(writeValue, binaryEncoder);
    }

    protected abstract Object writeValue(AvroWriter writer, T value, Type type) throws Exception;

    protected final Schema getSchema(Type type) {
        Specification specification = Specification.getSpecification(type);
        Class<?> clazz = TypeUtility.getRawType(type, null);
        Schema result = this.getFromCache(clazz);
        if (result == null) {
            return specification2SchemaMaps.get(specification).transformer(type, clazz);
        } else {
            return result;
        }
    }

    private Schema getFromCache(Class<?> clazz) {
        return type2Schemas.get(clazz);
    }

    private void addCache(Class<?> clazz, Schema schema) {
        type2Schemas.put(clazz, schema);
    }

    protected final Type[] getTypes(Type type, Class<?> clazz) {
        type = TypeUtility.refineType(type, clazz);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        return parameterizedType.getActualTypeArguments();
    }

    private Class<?> getTypeClazz(Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return clazz;
    }

    private final static Map<Class<?>, Schema> type2Schemas = new HashMap<>();

    private final static Map<Class<?>, Schema> arrayType2Schemas = new HashMap<>();

    private final static Map<Specification, TransFormerSchema<Type, Class<?>>> specification2SchemaMaps = new HashMap<>();

    static {

        arrayType2Schemas.put(byte.class, SchemaBuilder.builder().bytesType());
        arrayType2Schemas.put(Byte.class, SchemaBuilder.builder().bytesType());

        type2Schemas.put(Date.class, SchemaBuilder.builder().longType());
        type2Schemas.put(Instant.class, SchemaBuilder.builder().longType());

        type2Schemas.put(AtomicBoolean.class, SchemaBuilder.builder().booleanType());
        type2Schemas.put(Boolean.class, SchemaBuilder.builder().booleanType());
        type2Schemas.put(boolean.class, SchemaBuilder.builder().booleanType());

        type2Schemas.put(char.class, SchemaBuilder.builder().stringType());
        type2Schemas.put(Character.class, SchemaBuilder.builder().stringType());
        type2Schemas.put(String.class, SchemaBuilder.builder().stringType());

        type2Schemas.put(Double.class, SchemaBuilder.builder().doubleType());
        type2Schemas.put(double.class, SchemaBuilder.builder().doubleType());

        type2Schemas.put(BigInteger.class, SchemaBuilder.builder().stringType());
        type2Schemas.put(BigDecimal.class, SchemaBuilder.builder().stringType());

        type2Schemas.put(Float.class, SchemaBuilder.builder().floatType());
        type2Schemas.put(float.class, SchemaBuilder.builder().floatType());

        type2Schemas.put(AtomicInteger.class, SchemaBuilder.builder().intType());
        type2Schemas.put(Integer.class, SchemaBuilder.builder().intType());
        type2Schemas.put(int.class, SchemaBuilder.builder().intType());
        type2Schemas.put(Byte.class, SchemaBuilder.builder().intType());
        type2Schemas.put(byte.class, SchemaBuilder.builder().intType());

        type2Schemas.put(AtomicLong.class, SchemaBuilder.builder().longType());
        type2Schemas.put(Long.class, SchemaBuilder.builder().longType());
        type2Schemas.put(long.class, SchemaBuilder.builder().longType());
        type2Schemas.put(Short.class, SchemaBuilder.builder().longType());
        type2Schemas.put(short.class, SchemaBuilder.builder().longType());
    }

    {
        specification2SchemaMaps.put(Specification.ARRAY, (type, clazz) -> {
            Class<?> typeClazz;
            if (((typeClazz = getTypeClazz(clazz.getComponentType())) == Byte.class || typeClazz == byte.class)) {
                return arrayType2Schemas.get(typeClazz);
            } else {
                Schema schema = getSchema(clazz.getComponentType());
                if (schema.getType() == Schema.Type.ENUM) {
                    return SchemaBuilder.array().items().type(schema);
                }
                return SchemaBuilder.array().items().nullable().type(schema);
            }
        });

        specification2SchemaMaps.put(Specification.MAP, (type, clazz) -> {
            Type[] types = getTypes(type, Map.class);
            return SchemaBuilder.map().values().nullable().type(this.getSchema(types[1]));
        });

        specification2SchemaMaps.put(Specification.COLLECTION, (type, clazz) -> {
            Type[] types = getTypes(type, Collection.class);
            Schema schema = this.getSchema(types[0]);
            if (schema.getType() == Schema.Type.ENUM) {
                return SchemaBuilder.array().items().type(schema);
            }
            return SchemaBuilder.array().items().nullable().type(schema);
        });

        specification2SchemaMaps.put(Specification.ENUMERATION, (type, clazz) -> {
            Object[] enumConstants = clazz.getEnumConstants();
            String[] strEnums = new String[enumConstants.length];
            for (int i = 0; i < enumConstants.length; i++) {
                strEnums[i] = ((Enum<?>) enumConstants[i]).name();
            }
            Schema schema = SchemaBuilder.enumeration(clazz.getSimpleName()).symbols(strEnums);
            return schema;
        });

        specification2SchemaMaps.put(Specification.OBJECT, (type, clazz) -> {
            SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder.builder().record(clazz.getSimpleName()).fields();

            Field[] clazzFields = FieldUtils.getAllFields(clazz);
            for (Field clazzField : clazzFields) {
                fields.name(clazzField.getName()).type(this.getSchema(clazzField.getGenericType())).noDefault();
            }

            Schema schema = fields.endRecord();
            this.addCache(clazz, schema);
            return schema;
        });

        specification2SchemaMaps.put(Specification.TYPE, (type, clazz) -> SchemaBuilder.array().items().intType());

    }

    @FunctionalInterface
    private interface TransFormerSchema<T, C> {
        Schema transformer(Type type, Class<?> clazz);
    }

}
