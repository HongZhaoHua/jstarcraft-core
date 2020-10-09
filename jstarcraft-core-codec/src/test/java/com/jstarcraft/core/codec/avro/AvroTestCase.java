package com.jstarcraft.core.codec.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.data.TimeConversions.TimestampMillisConversion;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData.AllowNull;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificData;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.MockComplexObject;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.common.reflection.TypeUtility;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;

public class AvroTestCase {

    public static class TypeConversion extends Conversion {

        private Class clazz;

        private String name;

        public TypeConversion(Class clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public Class getConvertedType() {
            return clazz;
        }

        @Override
        public Schema getRecommendedSchema() {
            return new LogicalType(getLogicalTypeName()).addToSchema(Schema.create(Schema.Type.STRING));
        }

        @Override
        public String getLogicalTypeName() {
            return name;
        }

        @Override
        public Object fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
            return TypeUtility.string2Type(value.toString());
        }

        @Override
        public CharSequence toCharSequence(Object value, Schema schema, LogicalType type) {
            return TypeUtility.type2String((Type) value);
        }

    }

    private AllowNull avroData = new AllowNull() {

        @Override
        protected Schema createSchema(Type type, Map<String, Schema> names) {
            if (type instanceof Class) {
                Class<?> clazz = (Class<?>) type;
                if (clazz.isArray()) {
                    Class component = clazz.getComponentType();
                    if (component == Byte.TYPE) {
                        Schema schema = Schema.create(Schema.Type.BYTES);
                        schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                        return schema;
                    }
                    Schema schema = createSchema(component, names);
                    if (component.isPrimitive()) {
                        schema = SchemaBuilder.array().items().type(schema);
                    } else {
                        schema = SchemaBuilder.array().items().nullable().type(schema);
                    }
                    schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                    return schema;
                }
            }
            if (type instanceof GenericArrayType) {
                Type component = ((GenericArrayType) type).getGenericComponentType();
                if (component == Byte.TYPE) {
                    return Schema.create(Schema.Type.BYTES);
                }
                Schema schema = createSchema(component, names);
                schema = SchemaBuilder.array().items().nullable().type(schema);
                return schema;
            }
            Class clazz = TypeUtility.getRawType(type, null);
            if (Collection.class.isAssignableFrom(clazz)) {
                type = TypeUtility.refineType(type, Collection.class);
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                Schema schema = createSchema(types[0], names);
                schema = SchemaBuilder.array().items().nullable().type(schema);
                schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                return schema;
            }
            return super.createSchema(type, names);
        }

        @Override
        public <T> Conversion<T> getConversionByClass(Class<T> datumClass, LogicalType logicalType) {
            return (Conversion<T>) super.getConversionFor(logicalType);
        }

        @Override
        public int resolveUnion(Schema union, Object datum) {
            List<Schema> candidates = union.getTypes();
            for (int index = 0; index < candidates.size(); index += 1) {
                LogicalType candidateType = candidates.get(index).getLogicalType();
                if (candidateType != null) {
                    Conversion<?> conversion = super.getConversionFor(candidateType);
                    if (conversion != null) {
                        return index;
                    }
                }
            }
            Integer index = union.getIndexNamed(getSchemaName(datum));
            if (index != null) {
                return index;
            }
            throw new UnresolvedUnionException(union, datum);
        }

    };

    {
        avroData.addLogicalTypeConversion(new TimestampMillisConversion());
        avroData.addLogicalTypeConversion(new TypeConversion(Type.class, "type"));
        avroData.addLogicalTypeConversion(new TypeConversion(Class.class, "class"));
        avroData.addLogicalTypeConversion(new TypeConversion(GenericArrayType.class, "generic-array-type"));
        avroData.addLogicalTypeConversion(new TypeConversion(ParameterizedType.class, "parameterized-type"));
        avroData.addLogicalTypeConversion(new TypeConversion(TypeVariable.class, "type-variable"));
        avroData.addLogicalTypeConversion(new TypeConversion(WildcardType.class, "wildcard-type"));
    }

    protected void testConvert(Type type, Object value) throws Exception {
        Schema schema = avroData.getSchema(type);
        ReflectDatumWriter writer = new ReflectDatumWriter<>(schema, avroData);
        ReflectDatumReader reader = new ReflectDatumReader<>(schema, schema, avroData);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().directBinaryEncoder(output, null);
            writer.write(value, encoder);
            encoder.flush();
            byte[] data = output.toByteArray();
            try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                Decoder decoder = DecoderFactory.get().directBinaryDecoder(input, null);
                if (type == AtomicBoolean.class) {
                    AtomicBoolean left = (AtomicBoolean) value;
                    AtomicBoolean right = (AtomicBoolean) reader.read(null, decoder);
                    Assert.assertTrue(TypeUtility.isInstance(left, type));
                    Assert.assertTrue(TypeUtility.isInstance(right, type));
                    Assert.assertThat(right.get(), CoreMatchers.equalTo(left.get()));
                } else if (type == AtomicInteger.class || type == AtomicLong.class) {
                    Number left = (Number) value;
                    Number right = (Number) reader.read(null, decoder);
                    Assert.assertTrue(TypeUtility.isInstance(left, type));
                    Assert.assertTrue(TypeUtility.isInstance(right, type));
                    Assert.assertThat(right.longValue(), CoreMatchers.equalTo(left.longValue()));
                } else {
                    Object left = value;
                    Object right = reader.read(null, decoder);
                    if (value != null) {
                        Assert.assertTrue(TypeUtility.isInstance(left, type));
                        Assert.assertTrue(TypeUtility.isInstance(right, type));
                    }
                    Assert.assertThat(right, CoreMatchers.equalTo(left));
                }
            }
        }
    }

    @Test
    public void testAvro() throws Exception {
        MockComplexObject mock = MockComplexObject.instanceOf(0, "birdy", null, 10, Instant.now(), MockEnumeration.TERRAN);
        testConvert(MockComplexObject.class, mock);
    }

    @Test
    public void testType() throws Exception {
        // 基于数组类型测试
        Type type = TypeUtility.genericArrayType(MockComplexObject.class);
        testConvert(GenericArrayType.class, type);
        type = TypeUtility.genericArrayType(type);
        testConvert(GenericArrayType.class, type);
        type = TypeUtility.genericArrayType(byte.class);
        testConvert(GenericArrayType.class, type);
        type = TypeUtility.genericArrayType(Byte.class);
        testConvert(GenericArrayType.class, type);
        testConvert(MockComplexObject[].class.getClass(), MockComplexObject[].class);
        testConvert(byte[].class.getClass(), byte[].class);

        // 基于布尔类型测试
        type = AtomicBoolean.class;
        testConvert(type.getClass(), type);
        type = boolean.class;
        testConvert(type.getClass(), type);
        type = Boolean.class;
        testConvert(type.getClass(), type);

        // 基于集合类型测试
        type = TypeUtility.parameterize(ArrayList.class, MockComplexObject.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtility.parameterize(LinkedList.class, type);
        testConvert(ParameterizedType.class, type);
        type = TypeUtility.parameterize(HashSet.class, byte.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtility.parameterize(TreeSet.class, Byte.class);
        testConvert(ParameterizedType.class, type);

        // 基于枚举类型测试
        type = MockEnumeration.class;
        testConvert(type.getClass(), type);

        // 基于映射类型测试
        type = TypeUtility.parameterize(HashMap.class, String.class, MockComplexObject.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtility.parameterize(HashMap.class, String.class, type);
        testConvert(ParameterizedType.class, type);
        type = TypeUtility.parameterize(HashMap.class, byte.class, byte.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtility.parameterize(HashMap.class, Byte.class, Byte.class);
        testConvert(ParameterizedType.class, type);
        type = Byte2BooleanOpenHashMap.class;
        testConvert(Class.class, type);
        type = ByteArrayList.class;
        testConvert(Class.class, type);

        // 基于数值类型测试
        type = AtomicInteger.class;
        testConvert(type.getClass(), type);
        type = AtomicLong.class;
        testConvert(type.getClass(), type);
        type = byte.class;
        testConvert(type.getClass(), type);
        type = short.class;
        testConvert(type.getClass(), type);
        type = int.class;
        testConvert(type.getClass(), type);
        type = long.class;
        testConvert(type.getClass(), type);
        type = float.class;
        testConvert(type.getClass(), type);
        type = double.class;
        testConvert(type.getClass(), type);
        type = Byte.class;
        testConvert(type.getClass(), type);
        type = Short.class;
        testConvert(type.getClass(), type);
        type = Integer.class;
        testConvert(type.getClass(), type);
        type = Long.class;
        testConvert(type.getClass(), type);
        type = Float.class;
        testConvert(type.getClass(), type);
        type = Double.class;
        testConvert(type.getClass(), type);
        type = BigInteger.class;
        testConvert(type.getClass(), type);
        type = BigDecimal.class;
        testConvert(type.getClass(), type);

        // 基于对象类型测试
        type = MockComplexObject.class;
        testConvert(type.getClass(), type);

        // 基于字符串类型测试
        type = char.class;
        testConvert(type.getClass(), type);
        type = Character.class;
        testConvert(type.getClass(), type);
        type = String.class;
        testConvert(type.getClass(), type);

        // 基于时间类型测试
        type = Date.class;
        testConvert(type.getClass(), type);
        type = Instant.class;
        testConvert(type.getClass(), type);
    }

}
