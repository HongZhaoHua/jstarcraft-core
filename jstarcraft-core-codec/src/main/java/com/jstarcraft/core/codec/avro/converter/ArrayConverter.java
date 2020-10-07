package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 *
 * @author: MnZzV
 **/
public class ArrayConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        if (!TypeUtility.isArrayType(type)) {
            throw new CodecException("Avro解码类型不是数组");
        }
        final Class<?> clazz = (Class<?>) type;
        Class<?> typeClazz = clazz.getComponentType();
        typeClazz = typeClazz == null ? clazz : typeClazz;
        while (typeClazz != null && typeClazz.isArray()) {
            typeClazz = typeClazz.getComponentType();
        }

        final List<?> list = Byte.class.isAssignableFrom(typeClazz) || byte.class.isAssignableFrom(typeClazz) ? getByteList(input) : (List<?>) input;
        Object result = Array.newInstance(clazz.getComponentType(), list.size());
        AvroConverter<?> avroConverter = avroReader.getAvroConverter(Specification.getSpecification(clazz.getComponentType()));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                continue;
            }
            Array.set(result, i, avroConverter.readValue(avroReader, list.get(i), clazz.getComponentType()));
        }
        return result;
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object value, Type type) throws Exception {
        return getWriteList(writer, value, type);
    }

    private List<?> getByteList(Object input) {
        byte[] array = ((ByteBuffer) input).array();
        final ArrayList<Object> objects = Lists.newArrayList();
        for (byte b : array) {
            objects.add(b);
        }
        return objects;
    }

    private Object getWriteList(AvroWriter writer, Object content, Type type) throws Exception {
        Class baseClazz = TypeUtility.getRawType(type, null);
        Class typeClazz = baseClazz.getComponentType();
        typeClazz = typeClazz == null ? baseClazz : typeClazz;
        while (typeClazz.isArray()) {
            typeClazz = typeClazz.getComponentType();
        }
        if (byte.class.isAssignableFrom(typeClazz) || Byte.class.isAssignableFrom(typeClazz)) {
            int length;
            final Object o = Array.newInstance(byte.class, length = Array.getLength(content));
            for (int i = 0; i < length; i++) {
                Array.set(o, i, Array.get(content, i));
            }
            byte[] bytes = (byte[]) o;
            return ByteBuffer.wrap(bytes);
        }

        if (Object.class.isAssignableFrom(typeClazz)) {
            final ArrayList<Object> objects = Lists.newArrayList();
            for (Object o : (Object[]) content) {
                if (o == null) {
                    objects.add(null);
                    continue;
                }
                objects.add(writer.getAvroConverter(Specification.getSpecification(o.getClass())).writeValue(writer, o, typeClazz));

            }
            return objects;
        }
        if (typeClazz.isPrimitive() && baseClazz.isArray()) {
            int length = Array.getLength(content);
            List list = new ArrayList();
            for (int i = 0; i < length; i++) {
                final Object o = Array.get(content, i);
                list.add(this.getWriteList(writer, o, baseClazz.getComponentType()));
            }
            return list;
        } else {
            return content;
        }
    }

    private List<?> converterWrite(Object[] paramArr) {
        if (TypeUtility.isArrayType(paramArr[0].getClass())) {
            final ArrayList<Object> objects = Lists.newArrayList();
            for (int i = 0; i < paramArr.length; i++) {
                final Object[] indexArr = (Object[]) paramArr[0];
                objects.add(this.converterWrite(indexArr));
            }
            return objects;
        } else {
            return Arrays.asList(paramArr);
        }
    }

    private static Map<Class<?>, Class<?>> primitive2ObjectClass = new HashMap<>();

    static {
        primitive2ObjectClass.put(int.class, Integer.class);
        primitive2ObjectClass.put(boolean.class, Boolean.class);
        primitive2ObjectClass.put(byte.class, Byte.class);
        primitive2ObjectClass.put(double.class, Double.class);
        primitive2ObjectClass.put(float.class, Float.class);
        primitive2ObjectClass.put(long.class, Long.class);
    }
}
