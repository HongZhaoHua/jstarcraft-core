package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 类型转换器
 * 
 * @author Birdy
 *
 */
public class TypeConverter implements CsvConverter<Type> {

    @Override
    public Type readValueFrom(CsvReader context, Type type) throws Exception {
        Iterator<String> in = context.getInputStream();
        String element = in.next();
        if (StringUtility.isEmpty(element)) {
            return null;
        }
        return TypeUtility.string2Type(element);
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Type instance) throws Exception {
        CSVPrinter out = context.getOutputStream();
        if (instance == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        out.print(TypeUtility.type2String(instance));
    }
}
