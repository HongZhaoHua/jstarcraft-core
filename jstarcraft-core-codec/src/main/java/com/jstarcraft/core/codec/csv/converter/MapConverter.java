package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 映射转换器
 * 
 * @author Birdy
 *
 */
public class MapConverter implements CsvConverter<Map<Object, Object>> {

    @Override
    public Map<Object, Object> readValueFrom(CsvReader context, Type type) throws Exception {
        // TODO 处理null
        Iterator<String> in = context.getInputStream();
        String check = in.next();
        if (StringUtility.isEmpty(check)) {
            return null;
        }
        int length = Integer.valueOf(check);
        Class<?> clazz = TypeUtility.getRawType(type, null);
        // 兼容UniMi
        type = TypeUtility.refineType(type, Map.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        ClassDefinition definition = context.getClassDefinition(clazz);
        Map<Object, Object> instance = (Map) definition.getInstance();
        Class<?> keyClazz = TypeUtility.getRawType(types[0], null);
        CsvConverter keyConverter = context.getCsvConverter(Specification.getSpecification(keyClazz));
        Class<?> valueClazz = TypeUtility.getRawType(types[1], null);
        CsvConverter valueConverter = context.getCsvConverter(Specification.getSpecification(valueClazz));
        for (int index = 0; index < length; index++) {
            Object key = keyConverter.readValueFrom(context, types[0]);
            Object element = valueConverter.readValueFrom(context, types[1]);
            instance.put(key, element);
        }
        return instance;
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Map<Object, Object> instance) throws Exception {
        CSVPrinter out = context.getOutputStream();
        if (instance == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        // 兼容UniMi
        type = TypeUtility.refineType(type, Map.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        Map<Object, Object> map = Map.class.cast(instance);
        out.print(map.size());
        Class<?> keyClazz = TypeUtility.getRawType(types[0], null);
        CsvConverter keyConverter = context.getCsvConverter(Specification.getSpecification(keyClazz));
        Class<?> valueClazz = TypeUtility.getRawType(types[1], null);
        CsvConverter valueConverter = context.getCsvConverter(Specification.getSpecification(valueClazz));
        for (Entry<Object, Object> keyValue : map.entrySet()) {
            Object key = keyValue.getKey();
            keyConverter.writeValueTo(context, types[0], key);
            Object element = keyValue.getValue();
            valueConverter.writeValueTo(context, types[1], element);
        }
    }

}
