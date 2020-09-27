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
            Object instance = new Date(Long.valueOf(element));
            return instance;
        } else {
            Object instance = Instant.ofEpochMilli(Long.valueOf(element));
            return instance;
        }
    }

    @Override
    public void writeValueTo(CsvWriter context, Type type, Object instance) throws Exception {
        CSVPrinter out = context.getOutputStream();
        if (instance == null) {
            out.print(StringUtility.EMPTY);
            return;
        }
        // 处理日期类型
        if (TypeUtility.isAssignable(type, Date.class)) {
            instance = Date.class.cast(instance).getTime();
            out.print(instance);
            return;
        } else {
            instance = Instant.class.cast(instance).toEpochMilli();
            out.print(instance);
            return;
        }
    }

}
