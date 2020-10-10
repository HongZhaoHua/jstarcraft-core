package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 映射转换器
 * 
 * @author Yue Zhen Wei
 *
 */
@Deprecated
public class MapConverter extends AvroConverter<Map<Object, Object>> {

    @Override
    protected Map<Object, Object> readValue(AvroReader context, Object record, Type type) throws Exception {
        Class<?> rawType = TypeUtility.getRawType(type, null);
        Map<Object, Object> instance = (Map<Object, Object>) (rawType).newInstance();
        type = TypeUtility.refineType(type, Map.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        Class<?> keyClazz = (Class<?>) types[0];
        Class<?> valueClazz = (Class<?>) types[1];
        AvroConverter<?> keyConverter = context.getAvroConverter(Specification.getSpecification(keyClazz));
        AvroConverter<?> valueConverter = context.getAvroConverter(Specification.getSpecification(valueClazz));
        Iterator<Map.Entry<String, Object>> iterator = ((HashMap<String, Object>) record).entrySet().iterator();
        Map.Entry<String, Object> next;
        while (iterator.hasNext()) {
            next = iterator.next();
            instance.put(keyConverter.readValue(context, next.getKey(), keyClazz), valueConverter.readValue(context, next.getValue(), valueClazz));
        }
        return instance;
    }

    @Override
    protected Object writeValue(AvroWriter context, Map<Object, Object> instance, Type type) throws Exception {
        Type refineType = TypeUtility.refineType(type, Map.class);
        ParameterizedType cast = ParameterizedType.class.cast(refineType);
        Type actualTypeArgument = cast.getActualTypeArguments()[1];
        AvroConverter converter = context.getAvroConverter(Specification.getSpecification(actualTypeArgument));
        Class<?> rawType = TypeUtility.getRawType(type, null);
        Map record = (Map) context.getClassDefinition(rawType).getInstance();
        for (Object key : instance.keySet()) {
            record.put(key, converter.writeValue(context, instance.get(key), actualTypeArgument));
        }
        return record;
    }
}
