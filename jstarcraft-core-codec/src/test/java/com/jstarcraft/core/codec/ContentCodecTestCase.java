package com.jstarcraft.core.codec;

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
import java.util.Arrays;
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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.NumberUtility;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;

public abstract class ContentCodecTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract ContentCodec getContentCodec(CodecDefinition protocolDefinition);

    protected ContentCodec contentCodec;

    {
        Collection<Type> protocolClasses = new LinkedList<>();
        // 布尔规范
        protocolClasses.add(AtomicBoolean.class);
        protocolClasses.add(boolean.class);
        protocolClasses.add(Boolean.class);

        // 数值规范
        protocolClasses.add(AtomicInteger.class);
        protocolClasses.add(AtomicLong.class);
        protocolClasses.add(byte.class);
        protocolClasses.add(short.class);
        protocolClasses.add(int.class);
        protocolClasses.add(long.class);
        protocolClasses.add(float.class);
        protocolClasses.add(double.class);
        protocolClasses.add(Byte.class);
        protocolClasses.add(Short.class);
        protocolClasses.add(Integer.class);
        protocolClasses.add(Long.class);
        protocolClasses.add(Float.class);
        protocolClasses.add(Double.class);
        protocolClasses.add(BigInteger.class);
        protocolClasses.add(BigDecimal.class);

        // 字符规范
        protocolClasses.add(char.class);
        protocolClasses.add(Character.class);
        protocolClasses.add(String.class);

        // 日期时间规范
        protocolClasses.add(Date.class);
        protocolClasses.add(Instant.class);

        // 类型规范
        protocolClasses.add(Class.class);
        protocolClasses.add(GenericArrayType.class);
        protocolClasses.add(ParameterizedType.class);
        protocolClasses.add(TypeVariable.class);
        protocolClasses.add(WildcardType.class);

        // 未知规范
        protocolClasses.add(void.class);
        protocolClasses.add(Void.class);

        protocolClasses.add(Object.class);
        protocolClasses.add(MockComplexObject.class);
        protocolClasses.add(MockEnumeration.class);
        protocolClasses.add(MockMatrix.class);
        protocolClasses.add(MockSimpleObject.class);

        protocolClasses.add(ArrayList.class);
        protocolClasses.add(HashSet.class);
        protocolClasses.add(TreeSet.class);
        protocolClasses.add(Byte2BooleanOpenHashMap.class);
        protocolClasses.add(ByteArrayList.class);
        CodecDefinition definition = CodecDefinition.instanceOf(protocolClasses);
        contentCodec = this.getContentCodec(definition);
    }

    protected void testConvert(Type type, Object value) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            contentCodec.encode(type, value, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
                if (type == AtomicBoolean.class) {
                    AtomicBoolean left = (AtomicBoolean) value;
                    AtomicBoolean right = (AtomicBoolean) contentCodec.decode(type, byteArrayInputStream);
                    Assert.assertTrue(TypeUtility.isInstance(left, type));
                    Assert.assertTrue(TypeUtility.isInstance(right, type));
                    Assert.assertThat(right.get(), CoreMatchers.equalTo(left.get()));
                } else if (type == AtomicInteger.class || type == AtomicLong.class) {
                    Number left = (Number) value;
                    Number right = (Number) contentCodec.decode(type, byteArrayInputStream);
                    Assert.assertTrue(TypeUtility.isInstance(left, type));
                    Assert.assertTrue(TypeUtility.isInstance(right, type));
                    Assert.assertThat(right.longValue(), CoreMatchers.equalTo(left.longValue()));
                } else {
                    Object left = value;
                    Object right = contentCodec.decode(type, byteArrayInputStream);
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
    public void testNull() throws Exception {
        // 基于Null的协议测试
        // 基于对象的协议测试
        MockComplexObject object = MockComplexObject.instanceOf(0, "birdy", null, 10, Instant.now(), MockEnumeration.TERRAN);
        testConvert(MockComplexObject.class, null);
        testConvert(MockComplexObject.class, object);

        // 基于枚举的协议测试
        testConvert(MockEnumeration.class, null);

        // 基于数组的协议测试
        Integer[] integerArray = new Integer[] { 0, null };
        testConvert(Integer[].class, integerArray);
        MockComplexObject[] objectArray = new MockComplexObject[] { object, null };
        testConvert(MockComplexObject[].class, objectArray);

        // 基于集合的协议测试
        List<MockComplexObject> objectList = new ArrayList<>(objectArray.length);
        Collections.addAll(objectList, objectArray);
        testConvert(TypeUtility.parameterize(ArrayList.class, MockComplexObject.class), objectList);
        // testConvert(ArrayList.class, objectList);
        Set<MockComplexObject> objectSet = new HashSet<>(objectList);
        testConvert(TypeUtility.parameterize(HashSet.class, MockComplexObject.class), objectSet);
        // testConvert(HashSet.class, objectSet);

        // 基于映射的协议测试
        Map<String, MockComplexObject> map = new HashMap<>();
        map.put(object.getFirstName(), object);
        map.put("null", null);
        testConvert(TypeUtility.parameterize(HashMap.class, String.class, MockComplexObject.class), map);
        // testConvert(HashMap.class, map);
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
        testConvert(WildcardType.class, TypeUtility.wildcardType().withUpperBounds(type).build());
        testConvert(WildcardType.class, TypeUtility.wildcardType().withLowerBounds(type).build());
        testConvert(TypeVariable.class, TypeUtility.typeVariable(null, "T", type));

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

    @Test
    public void testUniMi() throws Exception {
        Byte2BooleanOpenHashMap map = new Byte2BooleanOpenHashMap();
        map.put((byte) 1, true);
        map.put((byte) -1, false);
        testConvert(Byte2BooleanOpenHashMap.class, map);

        ByteArrayList list = new ByteArrayList();
        list.add((byte) 1);
        list.add((byte) -1);
        testConvert(ByteArrayList.class, list);
    }

    private void testPerformance(ContentCodec contentCodec, Type type, Object instance) {
        byte[] data = contentCodec.encode(type, instance);
        String message = StringUtility.format("格式化{}大小:{},{}", type.getTypeName(), data.length, Arrays.toString(data));
        logger.debug(message);

        Instant now = null;
        int times = 1000;
        now = Instant.now();
        for (int index = 0; index < times; index++) {
            contentCodec.encode(type, instance);
        }
        logger.debug(StringUtility.format("编码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));

        now = Instant.now();
        for (int index = 0; index < times; index++) {
            contentCodec.decode(type, data);
        }
        logger.debug(StringUtility.format("解码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));
    }

    @Test
    public void testPerformance() {
        String message = StringUtility.format("[{}]编解码性能测试", contentCodec.getClass().getName());
        logger.debug(message);

        int size = 1000;

        Type type = MockComplexObject.class;
        Object instance = MockComplexObject.instanceOf(Integer.MAX_VALUE, "birdy", "hong", size, Instant.now(), MockEnumeration.TERRAN);
        testPerformance(contentCodec, type, instance);

        type = MockSimpleObject.class;
        instance = MockSimpleObject.instanceOf(0, "birdy");
        testPerformance(contentCodec, type, instance);

        String[] stringArray = new String[size];
        for (int index = 0; index < size; index++) {
            stringArray[index] = "mickey" + index;
        }
        type = String[].class;
        instance = stringArray;
        testPerformance(contentCodec, type, instance);

        ArrayList<String> stringList = new ArrayList<>(size);
        Collections.addAll(stringList, stringArray);
        type = TypeUtility.parameterize(ArrayList.class, String.class);
        instance = stringList;
        testPerformance(contentCodec, type, instance);

        HashSet<String> stringSet = new HashSet<>(size);
        Collections.addAll(stringSet, stringArray);
        type = TypeUtility.parameterize(HashSet.class, String.class);
        instance = stringSet;
        testPerformance(contentCodec, type, instance);
    }

}
