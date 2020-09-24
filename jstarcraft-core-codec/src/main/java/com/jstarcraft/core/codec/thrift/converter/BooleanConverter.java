package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 布尔转换器
 * 
 * @author Birdy
 *
 */
public class BooleanConverter extends ThriftConverter<Object> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 1111 1111(false标记) */
    private static final byte FALSE_MARK = (byte) 0xFF;

    /** 0000 0001(true标记) */
    private static final byte TRUE_MARK = (byte) 0x01;

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        byte mark = protocol.readByte();
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
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws Exception {
        TProtocol protocol = context.getProtocol();
        if (value == null) {
            protocol.writeByte(NULL_MARK);
            return;
        } else if (type == Boolean.class || type == boolean.class) {
            if ((Boolean) value) {
                protocol.writeByte(TRUE_MARK);
            } else {
                protocol.writeByte(FALSE_MARK);
            }
        } else if (type == AtomicBoolean.class) {
            if (((AtomicBoolean) value).get()) {
                protocol.writeByte(TRUE_MARK);
            } else {
                protocol.writeByte(FALSE_MARK);
            }
        }
    }

}
