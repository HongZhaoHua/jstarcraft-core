package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 类型转换器
 * 
 * @author Yue Zhen Wei
 *
 */
public class TypeConverter extends AvroConverter<Type> {

    @Override
    protected Type readValue(AvroReader context, Object record, Type type) throws Exception {
        List<Integer> inputList = (List<Integer>) record;
        Integer base = inputList.remove(0);
        ClassDefinition baseType;
        if (base == objectArrayIndex) {
            baseType = context.getClassDefinition(Class.class);
        } else {
            baseType = context.getClassDefinition(base);
        }
        if (baseType.getType() == Class.class) {
            if (base == objectArrayIndex) {
                Type readValue = readValue(context, inputList, type);
                return Array.newInstance(TypeUtility.getRawType(readValue, null), 0).getClass();
            }
        } else if (baseType.getType() == GenericArrayType.class) {
            return TypeUtility.genericArrayType(readValue(context, inputList, type));
        } else if (baseType.getType() == ParameterizedType.class) {
            ClassDefinition instance = context.getClassDefinition(inputList.remove(0));
            Type[] types = new Type[inputList.remove(0)];
            for (int i = 0; i < types.length; i++) {
                types[i] = readValue(context, inputList, type);
            }
            return TypeUtility.parameterize(instance.getType(), types);

        } else {
            return baseType.getType();
        }

        return null;
    }

    private static final int objectArrayIndex = -1;

    @Override
    protected List<Object> writeValue(AvroWriter context, Type instance, Type type) throws Exception {
        List<Object> objects = new ArrayList<>();
        if (instance instanceof Class) {
            Class<?> clazz = TypeUtility.getRawType(instance, null);
            if (clazz.isArray()) {
                objects.add(objectArrayIndex);
                objects.addAll(this.writeValue(context, TypeUtility.getArrayComponentType(clazz), type));
            } else {
                ClassDefinition definition = context.getClassDefinition(clazz);
                objects.add(definition.getCode());
            }
        } else if (instance instanceof GenericArrayType) {
            ClassDefinition definition = context.getClassDefinition(GenericArrayType.class);
            instance = TypeUtility.getArrayComponentType(instance);
            objects.add(definition.getCode());
            List<Object> itemList = this.writeValue(context, instance, type);
            objects.addAll(itemList);
        } else if (instance instanceof ParameterizedType) {
            objects.add(context.getClassDefinition(ParameterizedType.class).getCode());
            Class<?> rawType = TypeUtility.getRawType(instance, null);
            ClassDefinition definition = context.getClassDefinition(rawType);
            objects.add(definition.getCode());
            ParameterizedType parameterizedType = (ParameterizedType) instance;
            Type[] types = parameterizedType.getActualTypeArguments();
            objects.add(types.length);
            for (Type paramType : types) {
                List<Object> itemList = this.writeValue(context, paramType, type);
                objects.addAll(itemList);
            }
        } else {
            throw new CodecConvertionException();
        }
        return objects;
    }
}
