package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.PropertyDefinition;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 对象转换器
 * 
 * @author Birdy
 *
 */
public class ObjectConverter implements CsvConverter<Object> {

    @Override
    public Object readValueFrom(CsvReader context, Type type) throws Exception {
        // TODO 处理null
        Iterator<String> in = context.getInputStream();
        String check = in.next();
        if (StringUtility.isEmpty(check)) {
            return null;
        }
        int length = Integer.valueOf(check);
        // 处理对象类型
        Class<?> clazz = TypeUtility.getRawType(type, null);
        ClassDefinition definition = context.getClassDefinition(clazz);
        Object instance = definition.getInstance();
        for (PropertyDefinition property : definition.getProperties()) {
            CsvConverter converter = context.getCsvConverter(property.getSpecification());
            Object value = converter.readValueFrom(context, property.getType());
            property.setValue(instance, value);
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
        // 处理对象类型
        Class<?> clazz = TypeUtility.getRawType(type, null);
        ClassDefinition definition = context.getClassDefinition(clazz);
        int length = definition.getProperties().length;
        out.print(length);
        for (PropertyDefinition property : definition.getProperties()) {
            CsvConverter converter = context.getCsvConverter(property.getSpecification());
            Object value = property.getValue(instance);
            converter.writeValueTo(context, property.getType(), value);
        }
    }

}
