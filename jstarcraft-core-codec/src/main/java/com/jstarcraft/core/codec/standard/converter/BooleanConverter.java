package com.jstarcraft.core.codec.standard.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 布尔转换器
 * 
 * @author Birdy
 *
 */
public class BooleanConverter extends StandardConverter<Object> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(false标记) */
    private static final byte FALSE_MARK = (byte) 0x01;

    /** 0000 0002(true标记) */
    private static final byte TRUE_MARK = (byte) 0x02;

    @Override
    public Object readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws IOException {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == FALSE_MARK) {
            if (type == AtomicBoolean.class) {
                return new AtomicBoolean(false);
            }
            return false;
        } else if (mark == TRUE_MARK) {
            if (type == AtomicBoolean.class) {
                return new AtomicBoolean(true);
            }
            return true;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Object instance) throws IOException {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.BOOLEAN);
        if (instance == null) {
            out.write(information);
            return;
        }
        if (type == Boolean.class || type == boolean.class) {
            if ((Boolean) instance) {
                information |= TRUE_MARK;
                out.write(information);
            } else {
                information |= FALSE_MARK;
                out.write(information);
            }
        } else if (type == AtomicBoolean.class) {
            if (((AtomicBoolean) instance).get()) {
                information |= TRUE_MARK;
                out.write(information);
            } else {
                information |= FALSE_MARK;
                out.write(information);
            }
        }
    }

}
