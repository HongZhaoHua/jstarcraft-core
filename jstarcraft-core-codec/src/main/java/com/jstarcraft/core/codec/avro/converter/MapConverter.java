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
public class MapConverter extends AvroConverter<Map<Object, Object>> {

    @Override
    protected Map<Object, Object> readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        Class<?> rawType = TypeUtility.getRawType(type, null);
        Map<Object, Object> map = (Map<Object, Object>) (rawType).newInstance();
        type = TypeUtility.refineType(type, Map.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        Class<?> keyClazz = (Class<?>) types[0];
        Class<?> valueClazz = (Class<?>) types[1];

        AvroConverter<?> keyConverter = avroReader.getAvroConverter(Specification.getSpecification(keyClazz));
        AvroConverter<?> valueConverter = avroReader.getAvroConverter(Specification.getSpecification(valueClazz));

        Iterator<Map.Entry<String, Object>> iterator = ((HashMap<String, Object>) input).entrySet().iterator();
        Map.Entry<String, Object> next;
        while (iterator.hasNext()) {
            next = iterator.next();
            map.put(keyConverter.readValue(avroReader, next.getKey(), keyClazz), valueConverter.readValue(avroReader, next.getValue(), valueClazz));
        }
        return map;
    }

    @Override
    protected Object writeValue(AvroWriter writer, Map<Object, Object> value, Type type) throws Exception {
        Type refineType = TypeUtility.refineType(type, Map.class);
        ParameterizedType cast = ParameterizedType.class.cast(refineType);
        Type actualTypeArgument = cast.getActualTypeArguments()[1];
        AvroConverter avroConverter = writer.getAvroConverter(Specification.getSpecification(actualTypeArgument));
        Class<?> rawType = TypeUtility.getRawType(type, null);
        Map output = (Map) writer.getClassDefinition(rawType).getInstance();
        for (Object key : value.keySet()) {
            output.put(key, avroConverter.writeValue(writer, value.get(key), actualTypeArgument));
        }

        return output;
    }
}
