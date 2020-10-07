package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 数组转换器
 * 
 * @author Yue Zhen Wei
 *
 */
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
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index) == null) {
                continue;
            }
            Array.set(result, index, avroConverter.readValue(avroReader, list.get(index), clazz.getComponentType()));
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
        for (byte element : array) {
            objects.add(element);
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
            final Object array = Array.newInstance(byte.class, length = Array.getLength(content));
            for (int index = 0; index < length; index++) {
                Array.set(array, index, Array.get(content, index));
            }
            byte[] bytes = (byte[]) array;
            return ByteBuffer.wrap(bytes);
        }

        if (Object.class.isAssignableFrom(typeClazz)) {
            final ArrayList<Object> objects = Lists.newArrayList();
            for (Object element : (Object[]) content) {
                if (element == null) {
                    objects.add(null);
                    continue;
                }
                objects.add(writer.getAvroConverter(Specification.getSpecification(element.getClass())).writeValue(writer, element, typeClazz));
            }
            return objects;
        }
        if (typeClazz.isPrimitive() && baseClazz.isArray()) {
            int length = Array.getLength(content);
            List list = new ArrayList();
            for (int index = 0; index < length; index++) {
                final Object element = Array.get(content, index);
                list.add(this.getWriteList(writer, element, baseClazz.getComponentType()));
            }
            return list;
        } else {
            return content;
        }
    }

}
