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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.MockComplexObject;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.codec.MockMatrix;
import com.jstarcraft.core.codec.avro.conversion.AtomicBooleanConversion;
import com.jstarcraft.core.codec.avro.conversion.AtomicIntegerConversion;
import com.jstarcraft.core.codec.avro.conversion.AtomicLongConversion;
import com.jstarcraft.core.codec.avro.conversion.DateConversion;
import com.jstarcraft.core.codec.avro.conversion.TypeConversion;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.NumberUtility;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;

public class AvroTestCase {

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
        avroData.addLogicalTypeConversion(new AtomicBooleanConversion());
        
        avroData.addLogicalTypeConversion(new AtomicIntegerConversion());
        avroData.addLogicalTypeConversion(new AtomicLongConversion());
        
        avroData.addLogicalTypeConversion(new DateConversion());
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
    public void testArray() throws Exception {
        Object wrapArray = new Integer[] { 0, 1, 2, 3, 4 };
        testConvert(Integer[].class, wrapArray);
        wrapArray = new Integer[][] { { 0, 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };
        testConvert(Integer[][].class, wrapArray);
        Object primitiveArray = new int[] { 0, 1, 2, 3, 4 };
        testConvert(int[].class, primitiveArray);
        primitiveArray = new int[][] { { 0, 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };
        testConvert(int[][].class, primitiveArray);
        wrapArray = new Byte[] { 0, 1, 2, 3, 4 };
        testConvert(Byte[].class, wrapArray);
        primitiveArray = ContentCodecTestCase.class.getName().getBytes(StringUtility.CHARSET);
        testConvert(byte[].class, primitiveArray);
    }

    @Test
    public void testBoolean() throws Exception {
        testConvert(AtomicBoolean.class, new AtomicBoolean(true));
        testConvert(AtomicBoolean.class, new AtomicBoolean(false));
        testConvert(Boolean.class, true);
        testConvert(Boolean.class, false);
        testConvert(boolean.class, true);
        testConvert(boolean.class, false);
    }

    @Test
    public void testInstant() throws Exception {
        // 基于时间的协议测试
        Date date = new Date(0L);
        testConvert(Date.class, date);
        date = new Date();
        testConvert(Date.class, date);
        Instant instant = Instant.ofEpochMilli(0L);
        testConvert(Instant.class, instant);
        instant = Instant.ofEpochMilli(System.currentTimeMillis());
        testConvert(Instant.class, instant);
    }

    @Test
    public void testComplex() throws Exception {
        // 基于对象的协议测试
        MockComplexObject object = MockComplexObject.instanceOf(0, "birdy", "hong", 10, Instant.now(), MockEnumeration.TERRAN);
        testConvert(MockComplexObject.class, object);
        int dimension = 50;
        Table<Integer, Integer, Double> dataTable = HashBasedTable.create();
        for (int rowIndex = 0; rowIndex < dimension; rowIndex++) {
            for (int columnIndex = 0; columnIndex < dimension; columnIndex++) {
                if (RandomUtility.randomBoolean()) {
                    dataTable.put(rowIndex, columnIndex, 1D);
                }
            }
        }
        MockMatrix matrix = MockMatrix.instanceOf(dimension, dimension, dataTable);
        testConvert(MockMatrix.class, matrix);

        // 基于枚举的协议测试
        testConvert(MockEnumeration.class, MockEnumeration.TERRAN);

        // 基于数组的协议测试
        MockEnumeration[] enumerationArray = MockEnumeration.values();
        testConvert(MockEnumeration[].class, enumerationArray);
        MockComplexObject[] objectArray = new MockComplexObject[] { object };
        testConvert(MockComplexObject[].class, objectArray);

        // 基于集合的协议测试
        List<MockEnumeration> enumerationList = new ArrayList<>(enumerationArray.length);
        Collections.addAll(enumerationList, enumerationArray);
        testConvert(TypeUtility.parameterize(ArrayList.class, MockEnumeration.class), enumerationList);
        Set<MockEnumeration> enumerationSet = new HashSet<>(enumerationList);
        testConvert(TypeUtility.parameterize(HashSet.class, MockEnumeration.class), enumerationSet);

        List<Integer> integerList = new ArrayList<>(5);
        Collections.addAll(integerList, new Integer[] { 0, 1, 2, 3, 4 });
        testConvert(TypeUtility.parameterize(ArrayList.class, Integer.class), integerList);
        Set<Integer> integerSet = new TreeSet<>(integerList);
        testConvert(TypeUtility.parameterize(TreeSet.class, Integer.class), integerSet);

        List<MockComplexObject> objectList = new ArrayList<>(objectArray.length);
        Collections.addAll(objectList, objectArray);
        testConvert(TypeUtility.parameterize(ArrayList.class, MockComplexObject.class), objectList);
        Set<MockComplexObject> objectSet = new HashSet<>(objectList);
        testConvert(TypeUtility.parameterize(HashSet.class, MockComplexObject.class), objectSet);

        // 基于映射的协议测试
        Map<String, MockComplexObject> map = new HashMap<>();
        for (MockComplexObject element : objectList) {
            map.put(element.getFirstName(), element);
        }
        testConvert(TypeUtility.parameterize(HashMap.class, String.class, MockComplexObject.class), map);
    }

    @Test
    public void testNumber() throws Exception {
        testConvert(AtomicInteger.class, new AtomicInteger(Short.MIN_VALUE));
        testConvert(AtomicInteger.class, new AtomicInteger(Short.MAX_VALUE));
        testConvert(AtomicInteger.class, new AtomicInteger(0));
        testConvert(AtomicInteger.class, new AtomicInteger(Integer.MIN_VALUE));
        testConvert(AtomicInteger.class, new AtomicInteger(Integer.MAX_VALUE));

        testConvert(AtomicLong.class, new AtomicLong(Integer.MIN_VALUE));
        testConvert(AtomicLong.class, new AtomicLong(Integer.MAX_VALUE));
        testConvert(AtomicLong.class, new AtomicLong(0));
        testConvert(AtomicLong.class, new AtomicLong(Long.MIN_VALUE));
        testConvert(AtomicLong.class, new AtomicLong(Long.MAX_VALUE));

        // 基于数值的协议测试
        testConvert(Byte.class, NumberUtility.convert(0, Byte.class));
        testConvert(Byte.class, Byte.MIN_VALUE);
        testConvert(Byte.class, Byte.MAX_VALUE);
        testConvert(Short.class, NumberUtility.convert(Byte.MIN_VALUE, Short.class));
        testConvert(Short.class, NumberUtility.convert(Byte.MAX_VALUE, Short.class));
        testConvert(Short.class, NumberUtility.convert(0, Short.class));
        testConvert(Short.class, Short.MIN_VALUE);
        testConvert(Short.class, Short.MAX_VALUE);
        testConvert(Integer.class, NumberUtility.convert(Short.MIN_VALUE, Integer.class));
        testConvert(Integer.class, NumberUtility.convert(Short.MAX_VALUE, Integer.class));
        testConvert(Integer.class, NumberUtility.convert(0, Integer.class));
        testConvert(Integer.class, Integer.MIN_VALUE);
        testConvert(Integer.class, Integer.MAX_VALUE);
        testConvert(Long.class, NumberUtility.convert(Integer.MIN_VALUE, Long.class));
        testConvert(Long.class, NumberUtility.convert(Integer.MAX_VALUE, Long.class));
        testConvert(Long.class, NumberUtility.convert(0, Long.class));
        testConvert(Long.class, Long.MIN_VALUE);
        testConvert(Long.class, Long.MAX_VALUE);

        testConvert(Float.class, NumberUtility.convert(Long.MIN_VALUE, Float.class));
        testConvert(Float.class, NumberUtility.convert(Long.MAX_VALUE, Float.class));
        testConvert(Float.class, NumberUtility.convert(0, Float.class));
        testConvert(Float.class, Float.MIN_VALUE);
        testConvert(Float.class, Float.MAX_VALUE);
        testConvert(Double.class, NumberUtility.convert(Float.MIN_VALUE, Double.class));
        testConvert(Double.class, NumberUtility.convert(Float.MAX_VALUE, Double.class));
        testConvert(Double.class, NumberUtility.convert(0, Double.class));
        testConvert(Double.class, Double.MIN_VALUE);
        testConvert(Double.class, Double.MAX_VALUE);
        BigInteger bigInteger = new BigInteger(String.valueOf(Long.MAX_VALUE));
        bigInteger = bigInteger.add(bigInteger);
        testConvert(BigInteger.class, bigInteger);
        BigDecimal bigDecimal = new BigDecimal(bigInteger);
        bigDecimal = bigDecimal.add(bigDecimal);
        testConvert(BigDecimal.class, bigDecimal);
    }

    @Test
    public void testString() throws Exception {
        testConvert(char.class, ' ');
        testConvert(Character.class, ' ');
        testConvert(String.class, "string");
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
