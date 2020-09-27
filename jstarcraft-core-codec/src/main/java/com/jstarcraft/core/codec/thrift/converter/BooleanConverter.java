package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;

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
        protocol.readStructBegin();
        protocol.readFieldBegin();
        byte mark = protocol.readByte();
        protocol.readFieldEnd();
        protocol.readFieldBegin();
        protocol.readStructEnd();
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
        } else {
            return null;
        }
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws Exception {
        TProtocol protocol = context.getProtocol();
        byte mark;
        if (value == null) {
            mark = NULL_MARK;
        } else if (type == AtomicBoolean.class) {
            if (((AtomicBoolean) value).get()) {
                mark = TRUE_MARK;
            } else {
                mark = FALSE_MARK;
            }
        } else {
            if ((Boolean) value) {
                mark = TRUE_MARK;
            } else {
                mark = FALSE_MARK;
            }
        }
        protocol.writeStructBegin(new TStruct(definition.getName()));
        protocol.writeFieldBegin(new TField("_mark", TType.BYTE, (short) (1)));
        protocol.writeByte(mark);
        protocol.writeFieldEnd();
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
