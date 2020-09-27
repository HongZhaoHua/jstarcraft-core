package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 数组转换器
 * 
 * @author Birdy
 *
 */
public class ArrayConverter implements CsvConverter<Object> {

    @Override
    public Object readValueFrom(CsvReader context, Type type) throws Exception {
        // TODO 处理null
        Iterator<String> in = context.getInputStream();
        String check = in.next();
        if (StringUtility.isEmpty(check)) {
            return null;
        }
        int length = Integer.valueOf(check);
        Class<?> componentClass = null;
        Type componentType = null;
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = GenericArrayType.class.cast(type);
            componentType = genericArrayType.getGenericComponentType();
            componentClass = TypeUtility.getRawType(componentType, null);
        } else {
            Class<?> clazz = TypeUtility.getRawType(type, null);
            componentType = clazz.getComponentType();
            componentClass = clazz.getComponentType();
        }
        Object instance = Array.newInstance(componentClass, length);
        Specification specification = Specification.getSpecification(componentClass);
        CsvConverter converter = context.getCsvConverter(specification);
        for (int index = 0; index < length; index++) {
            Object element = converter.readValueFrom(context, componentType);
            Array.set(instance, index, element);
        }
        return instance;
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Object instance) throws Exception {
        // TODO 处理null
        CSVPrinter out = context.getOutputStream();
        if (instance == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        Class<?> componentClass = null;
        Type componentType = null;
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = GenericArrayType.class.cast(type);
            componentType = genericArrayType.getGenericComponentType();
            componentClass = TypeUtility.getRawType(componentType, null);
        } else {
            Class<?> clazz = TypeUtility.getRawType(type, null);
            componentType = clazz.getComponentType();
            componentClass = clazz.getComponentType();
        }
        int length = Array.getLength(instance);
        out.print(length);
        Specification specification = Specification.getSpecification(componentClass);
        CsvConverter converter = context.getCsvConverter(specification);
        for (int index = 0; index < length; index++) {
            Object element = Array.get(instance, index);
            converter.writeValueTo(context, componentType, element);
        }
    }

}
