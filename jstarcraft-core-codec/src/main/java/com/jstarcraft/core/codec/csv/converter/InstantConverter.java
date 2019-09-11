package com.jstarcraft.core.codec.csv.converter;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.csv.CSVPrinter;

import com.jstarcraft.core.codec.csv.CsvReader;
import com.jstarcraft.core.codec.csv.CsvWriter;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 时间转换器
 * 
 * @author Birdy
 *
 */
public class InstantConverter implements CsvConverter<Object> {

    @Override
    public Object readValueFrom(CsvReader context, Type type) throws Exception {
        Iterator<String> in = context.getInputStream();
        String element = in.next();
        if (StringUtility.isEmpty(element)) {
            return null;
        }
        // 处理日期类型
        if (TypeUtility.isAssignable(type, Date.class)) {
            Object value = new Date(Long.valueOf(element));
            return value;
        } else {
            Object value = Instant.ofEpochMilli(Long.valueOf(element));
            return value;
        }
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Object value) throws Exception {
        CSVPrinter out = context.getOutputStream();
        if (value == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        // 处理日期类型
        if (TypeUtility.isAssignable(type, Date.class)) {
            value = Date.class.cast(value).getTime();
            out.print(value);
            return;
        } else {
            value = Instant.class.cast(value).toEpochMilli();
            out.print(value);
            return;
        }
    }

}
