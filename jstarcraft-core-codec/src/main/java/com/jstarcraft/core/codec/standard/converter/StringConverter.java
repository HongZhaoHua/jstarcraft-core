package com.jstarcraft.core.codec.standard.converter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.utility.PressUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 字符串转换器
 * 
 * @author Birdy
 *
 */
public class StringConverter extends StandardConverter<Object> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0000(字符串标记) */
    private static final byte STRING_MARK = (byte) 0x01;

    /** 0000 0001(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x02;

    /** 0000 0002(Zip标记) */
    private static final byte ZIP_MARK = (byte) 0x03;

    /** Zip限制 */
    private static final Integer ZIP_LIMIT = 1024;

    public Object readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws IOException {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == STRING_MARK) {
            int length = NumberConverter.readNumber(in).intValue();
            if (in.available() < length) {
                throw new EOFException();
            }
            byte[] bytes = new byte[length];
            IoUtility.read(in, bytes);
            String value = new String(bytes, StringUtility.CHARSET);
            context.putStringValue(value);
            if (type == char.class || type == Character.class) {
                return value.charAt(0);
            } else {
                return value;
            }
        } else if (mark == REFERENCE_MARK) {
            int reference = NumberConverter.readNumber(in).intValue();
            String value = context.getStringValue(reference);
            if (type == char.class || type == Character.class) {
                return value.charAt(0);
            } else {
                return value;
            }
        } else if (mark == ZIP_MARK) {
            int length = NumberConverter.readNumber(in).intValue();
            if (in.available() < length) {
                throw new EOFException();
            }
            byte[] bytes = new byte[length];
            IoUtility.read(in, bytes);
            bytes = PressUtility.unzip(bytes, 30, TimeUnit.SECONDS);
            String value = new String(bytes, StringUtility.CHARSET);
            context.putStringValue(value);
            if (type == char.class || type == Character.class) {
                return value.charAt(0);
            } else {
                return value;
            }
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Object instance) throws IOException {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.STRING);
        if (instance == null) {
            out.write(information);
            return;
        }
        String element;
        if (type == char.class || type == Character.class) {
            element = String.valueOf(instance);
        } else {
            element = (String) instance;
        }
        int reference = context.getStringIndex(element);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            out.write(information);
            NumberConverter.writeNumber(out, reference);
        } else {
            context.putStringValue(element);
            byte[] bytes = element.getBytes(StringUtility.CHARSET);
            if (bytes.length > ZIP_LIMIT) {
                information |= ZIP_MARK;
                bytes = PressUtility.zip(bytes, 5);
            } else {
                information |= STRING_MARK;
            }
            out.write(information);
            int length = bytes.length;
            NumberConverter.writeNumber(out, length);
            IoUtility.write(bytes, out);
        }
    }

}
