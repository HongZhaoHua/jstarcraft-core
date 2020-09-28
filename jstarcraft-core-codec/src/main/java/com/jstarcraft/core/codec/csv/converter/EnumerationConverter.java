package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 枚举转换器
 * 
 * @author Birdy
 *
 */
public class EnumerationConverter implements CsvConverter<Object> {

    @Override
    public Object readValueFrom(CsvReader context, Type type) throws Exception {
        Iterator<String> in = context.getInputStream();
        String element = in.next();
        if (StringUtility.isEmpty(element)) {
            return null;
        }
        int index = Integer.valueOf(element);
        Class<?> clazz = TypeUtility.getRawType(type, null);
        return clazz.getEnumConstants()[index];
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Object instance) throws Exception {
        CSVPrinter out = context.getOutputStream();
        if (instance == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        Enum<?> enumeration = (Enum<?>) instance;
        int index = enumeration.ordinal();
        out.print(index);
    }

}
