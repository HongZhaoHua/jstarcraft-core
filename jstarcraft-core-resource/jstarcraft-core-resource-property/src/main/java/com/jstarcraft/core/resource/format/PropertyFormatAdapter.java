package com.jstarcraft.core.resource.format;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.exception.StorageException;

/**
 * Properties适配器
 * 
 * @author Birdy
 *
 */
public class PropertyFormatAdapter implements FormatAdapter {

    private final static Logger logger = LoggerFactory.getLogger(PropertyFormatAdapter.class);

    private final static String dot = "\\.";

    private final Pattern pattern = Pattern.compile("([^\\[]+)(?:\\[(.+)\\])?$");

    private void process(Object instanceObject, Type instanceType, String fieldName, LinkedList<String> fieldNames, String fieldValue) throws Exception {
        Class<?> instanceClazz = TypeUtility.getRawType(instanceType, null);
        final Matcher matcher = pattern.matcher(fieldName);
        if (matcher.find()) {
            if (matcher.group(2) == null) {
                // 对象或者值
                Field field = ReflectionUtility.findField(instanceClazz, matcher.group(1));
                field.setAccessible(true);
                if (fieldNames.isEmpty()) {
                    Type fieldType = field.getGenericType();
                    if (fieldType instanceof TypeVariable) {
                        // TODO 此处代码需要优化
                        HashMap<String, Type> types = new HashMap<>();
                        TypeVariable<?>[] typeVariables = instanceClazz.getTypeParameters();
                        ParameterizedType parameterizedType = ParameterizedType.class.cast(instanceType);
                        for (int index = 0; index < typeVariables.length; index++) {
                            types.put(typeVariables[index].getName(), parameterizedType.getActualTypeArguments()[index]);
                        }
                        TypeVariable<?> typeVariable = TypeVariable.class.cast(fieldType);
                        field.set(instanceObject, ConversionUtility.convert(fieldValue, types.get(typeVariable.getName())));
                    } else {
                        field.set(instanceObject, ConversionUtility.convert(fieldValue, fieldType));
                    }
                } else {
                    Object property = field.get(instanceObject);
                    instanceType = field.getGenericType();
                    instanceClazz = field.getType();
                    fieldName = fieldNames.pollFirst();
                    if (property == null) {
                        Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                        ReflectionUtility.makeAccessible(constructor);
                        property = constructor.newInstance();
                        field.set(instanceObject, property);
                    }
                    process(property, instanceType, fieldName, fieldNames, fieldValue);
                }
            } else {
                // 映射,集合或者数组
                Field field = ReflectionUtility.findField(instanceClazz, matcher.group(1));
                field.setAccessible(true);
                instanceType = field.getGenericType();
                instanceClazz = field.getType();
                if (fieldNames.isEmpty()) {
                    if (instanceClazz.isArray()) {
                        Object property = field.get(instanceObject);
                        instanceType = GenericArrayType.class.cast(instanceType).getGenericComponentType();
                        instanceClazz = instanceClazz.getComponentType();
                        if (property == null) {
                            property = Array.newInstance(instanceClazz, 1);
                            if (Integer.valueOf(matcher.group(2)) != 0) {
                                throw new StorageException();
                            }
                            Array.set(property, 0, ConversionUtility.convert(fieldValue, instanceType));
                            field.set(instanceObject, property);
                        } else {
                            Object oldArray = property;
                            int size = Array.getLength(oldArray);
                            Object newArray = Array.newInstance(instanceClazz, size + 1);
                            for (int index = 0; index < size; index++) {
                                Array.set(newArray, index, Array.get(oldArray, index));
                            }
                            Array.set(newArray, size, ConversionUtility.convert(fieldValue, instanceType));
                            property = newArray;
                            field.set(instanceObject, property);
                        }
                        return;
                    }
                    if (Map.class.isAssignableFrom(instanceClazz)) {
                        Type[] types = ParameterizedType.class.cast(instanceType).getActualTypeArguments();
                        Type keyType = types[0];
                        Type valueType = types[1];
                        Map map = Map.class.cast(field.get(instanceObject));
                        if (map == null) {
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            map = Map.class.cast(constructor.newInstance());
                            field.set(instanceObject, map);
                        }
                        Object key = ConversionUtility.convert(matcher.group(2), keyType);
                        Object value = ConversionUtility.convert(fieldValue, valueType);
                        map.put(key, value);
                        return;
                    }
                    // TODO 暂时无法完整支持所有Collection类型
                    if (List.class.isAssignableFrom(instanceClazz)) {
                        Type[] types = ParameterizedType.class.cast(instanceType).getActualTypeArguments();
                        Type elementType = types[0];
                        List list = List.class.cast(field.get(instanceObject));
                        if (list == null) {
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            list = List.class.cast(constructor.newInstance());
                            field.set(instanceObject, list);
                        }
                        list.add(ConversionUtility.convert(fieldValue, elementType));
                        return;
                    }
                } else {
                    if (instanceClazz.isArray()) {
                        Object property = field.get(instanceObject);
                        instanceType = GenericArrayType.class.cast(instanceType).getGenericComponentType();
                        instanceClazz = instanceClazz.getComponentType();
                        if (property == null) {
                            property = Array.newInstance(instanceClazz, 1);
                            if (Integer.valueOf(matcher.group(2)) != 0) {
                                throw new StorageException();
                            }
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            Object element = constructor.newInstance();
                            Array.set(property, 0, element);
                            field.set(instanceObject, property);

                            fieldName = fieldNames.pollFirst();
                            process(element, instanceType, fieldName, fieldNames, fieldValue);
                        } else {
                            int size = Array.getLength(property);
                            if (Integer.valueOf(matcher.group(2)) == size) {
                                Object oldArray = property;
                                Object newArray = Array.newInstance(instanceClazz, size + 1);
                                for (int index = 0; index < size; index++) {
                                    Array.set(newArray, index, Array.get(oldArray, index));
                                }
                                Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                                ReflectionUtility.makeAccessible(constructor);
                                Object element = constructor.newInstance();
                                Array.set(newArray, size, element);
                                property = newArray;
                                field.set(instanceObject, property);
                            }
                            Object element = Array.get(property, Integer.valueOf(matcher.group(2)));

                            fieldName = fieldNames.pollFirst();
                            process(element, instanceType, fieldName, fieldNames, fieldValue);
                        }
                        return;
                    }
                    if (Map.class.isAssignableFrom(instanceClazz)) {
                        Type[] types = ParameterizedType.class.cast(instanceType).getActualTypeArguments();
                        Type keyType = types[0];
                        Type valueType = types[1];
                        Map map = Map.class.cast(field.get(instanceObject));
                        if (map == null) {
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            map = Map.class.cast(constructor.newInstance());
                            field.set(instanceObject, map);
                        }
                        Object key = ConversionUtility.convert(matcher.group(2), keyType);
                        Object value = map.get(key);
                        if (value == null) {
                            instanceClazz = TypeUtility.getRawType(valueType, null);
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            value = constructor.newInstance();
                            map.put(key, value);
                        }
                        fieldName = fieldNames.pollFirst();
                        process(value, valueType, fieldName, fieldNames, fieldValue);
                        return;
                    }
                    // TODO 暂时无法完整支持所有Collection类型
                    if (List.class.isAssignableFrom(instanceClazz)) {
                        Type[] types = ParameterizedType.class.cast(instanceType).getActualTypeArguments();
                        Type elementType = types[0];
                        List list = List.class.cast(field.get(instanceObject));
                        if (list == null) {
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            list = List.class.cast(constructor.newInstance());
                            field.set(instanceObject, list);
                        }
                        Integer key = Integer.valueOf(matcher.group(2));
                        Object value = list.size() == key ? null : list.get(key);
                        if (value == null) {
                            instanceClazz = TypeUtility.getRawType(elementType, null);
                            Constructor<?> constructor = instanceClazz.getDeclaredConstructor();
                            ReflectionUtility.makeAccessible(constructor);
                            value = constructor.newInstance();
                            list.add(value);
                        }
                        fieldName = fieldNames.pollFirst();
                        process(value, elementType, fieldName, fieldNames, fieldValue);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        // 实例列表
        HashMap<Object, E> instanceObjects = new HashMap<>();
        try {
            Properties properties = new Properties();
            properties.load(stream);
            Constructor<E> constructor = clazz.getDeclaredConstructor();
            ReflectionUtility.makeAccessible(constructor);

            Field storageId = ReflectionUtility.uniqueField(clazz, ResourceId.class);
            storageId.setAccessible(true);

            TreeMap<?, ?> keyValues = new TreeMap<>(properties);
            for (Entry<?, ?> keyValue : keyValues.entrySet()) {
                LinkedList<String> fieldNames = new LinkedList<>(Arrays.asList(String.class.cast(keyValue.getKey()).split(dot)));
                String fieldName = fieldNames.pollFirst();
                String fieldValue = String.class.cast(keyValue.getValue());
                Object instanceId = ConversionUtility.convert(fieldName, storageId.getGenericType());
                E instanceObject = instanceObjects.get(instanceId);
                if (instanceObject == null) {
                    instanceObject = constructor.newInstance();
                    storageId.set(instanceObject, instanceId);
                    instanceObjects.put(instanceId, instanceObject);
                }
                if (fieldNames.isEmpty()) {
                    continue;
                } else {
                    fieldName = fieldNames.pollFirst();
                    process(instanceObject, clazz, fieldName, fieldNames, fieldValue);
                }
            }
            return instanceObjects.values().iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历Properties异常", exception);
        }
    }

}
