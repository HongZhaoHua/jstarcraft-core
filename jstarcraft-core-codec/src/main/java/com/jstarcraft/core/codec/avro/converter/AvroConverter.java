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
import java.util.function.BiFunction;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
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
@Deprecated
public abstract class AvroConverter<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract T readValue(AvroReader context, Object record, Type type) throws Exception;

    /**
     * 从指定上下文读取内容
     *
     * @param context
     * @param type
     * @return
     * @throws IOException
     */
    public final T readValueFrom(AvroReader context, Type type) throws Exception {
        Schema schema = getSchema(type);
        GenericDatumReader reader = new GenericDatumReader<>(schema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, context.getInputStream());
//        Decoder decoder = DecoderFactory.get().directBinaryDecoder(context.getInputStream(), null);
        Object record = reader.read(null, decoder);
        T instance = readValue(context, record, type);
        return instance;
    }

    protected abstract Object writeValue(AvroWriter context, T instance, Type type) throws Exception;

    /**
     * 将指定内容写到上下文
     *
     * @param context
     * @param type
     * @param instance
     * @throws IOException
     */
    public final void writeValueTo(AvroWriter context, Type type, T instance) throws Exception {
        Object record = writeValue(context, instance, type);
        Schema schema = getSchema(type);
        SpecificDatumWriter<Object> write = new SpecificDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().jsonEncoder(schema, context.getOutputStream());
//        Encoder encoder = EncoderFactory.get().directBinaryEncoder(context.getOutputStream(), null);
        write.write(record, encoder);
        encoder.flush();
    }

    // TODO 考虑使用ReflectData.getSchema替代
    protected final Schema getSchema(Type type) {
        Specification specification = Specification.getSpecification(type);
        Class<?> clazz = TypeUtility.getRawType(type, null);
        Schema schema = type2Schemas.get(clazz);
        if (schema == null) {
            schema = specification2Schemas.get(specification).apply(type, clazz);
        }
        return schema;
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

    private final static Map<Specification, BiFunction<Type, Class<?>, Schema>> specification2Schemas = new HashMap<>();

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
        specification2Schemas.put(Specification.ARRAY, (type, clazz) -> {
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

        specification2Schemas.put(Specification.MAP, (type, clazz) -> {
            Type[] types = getTypes(type, Map.class);
            return SchemaBuilder.map().values().nullable().type(this.getSchema(types[1]));
        });

        specification2Schemas.put(Specification.COLLECTION, (type, clazz) -> {
            Type[] types = getTypes(type, Collection.class);
            Schema schema = this.getSchema(types[0]);
            if (schema.getType() == Schema.Type.ENUM) {
                return SchemaBuilder.array().items().type(schema);
            }
            return SchemaBuilder.array().items().nullable().type(schema);
        });

        specification2Schemas.put(Specification.ENUMERATION, (type, clazz) -> {
            Object[] enumConstants = clazz.getEnumConstants();
            String[] strEnums = new String[enumConstants.length];
            for (int i = 0; i < enumConstants.length; i++) {
                strEnums[i] = ((Enum<?>) enumConstants[i]).name();
            }
            Schema schema = SchemaBuilder.enumeration(clazz.getSimpleName()).symbols(strEnums);
            return schema;
        });

        specification2Schemas.put(Specification.OBJECT, (type, clazz) -> {
            SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder.builder().record(clazz.getSimpleName()).fields();
            Field[] clazzFields = FieldUtils.getAllFields(clazz);
            for (Field clazzField : clazzFields) {
                fields.name(clazzField.getName()).type(this.getSchema(clazzField.getGenericType())).noDefault();
            }
            Schema schema = fields.endRecord();
            type2Schemas.put(clazz, schema);
            return schema;
        });

        specification2Schemas.put(Specification.TYPE, (type, clazz) -> SchemaBuilder.builder().stringType());
    }

}
