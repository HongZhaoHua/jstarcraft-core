package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 布尔转换器
 * 
 * @author Birdy
 *
 */
public class BooleanConverter implements CsvConverter<Object> {

    public static final String TRUE = "1";

    public static final String FALSE = "0";

    @Override
    public Object readValueFrom(CsvReader context, Type type) throws Exception {
        // TODO 处理null
        Iterator<String> in = context.getInputStream();
        String element = in.next();
        if (StringUtility.isEmpty(element)) {
            return null;
        }
        if (type == AtomicBoolean.class) {
            return new AtomicBoolean(element.equals(TRUE));
        }
        return element.equals(TRUE);
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Object instance) throws Exception {
        // TODO 处理null
        CSVPrinter out = context.getOutputStream();
        if (instance == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        if (type == AtomicBoolean.class) {
            instance = ((AtomicBoolean) instance).get();
        }
        out.print((instance.equals(true)) ? TRUE : FALSE);
    }

}
