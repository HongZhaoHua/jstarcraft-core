package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.sun.tools.classfile.Opcode.Set;

/**
 * 集合转换器
 * 
 * @author Birdy
 *
 */
public class CollectionConverter extends ThriftConverter<Collection<?>> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    @Override
    public byte getThriftType(Type type) {
        if (TypeUtility.isAssignable(type, Set.class)) {
            return TType.SET;
        }
        if (TypeUtility.isAssignable(type, List.class)) {
            return TType.LIST;
        }
        throw new CodecConvertionException();
    }

    @Override
    public Collection<?> readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        byte mark = protocol.readByte();
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = protocol.readI32();
            Collection instance = (Collection) definition.getInstance();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type elementType = types[0];
            ThriftConverter converter = context.getProtocolConverter(Specification.getSpecification(elementType));
            definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
            for (int index = 0; index < size; index++) {
                Object element = converter.readValueFrom(context, elementType, definition);
                instance.add(element);
            }
            return instance;
        } else if (mark == IMPLICIT_MARK) {
            int size = protocol.readI32();
            Collection instance = (Collection) definition.getInstance();
            for (int index = 0; index < size; index++) {
                int code = protocol.readI32();
                definition = context.getClassDefinition(code);
                ThriftConverter converter = context.getProtocolConverter(definition.getSpecification());
                Object element = converter.readValueFrom(context, definition.getType(), definition);
                instance.add(element);
            }
            return instance;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Collection<?> instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        byte mark = NULL_MARK;
        if (instance == null) {
            protocol.writeByte(mark);
            return;
        }
        if (type instanceof Class) {
            mark = IMPLICIT_MARK;
            protocol.writeByte(mark);
            int size = instance.size();
            protocol.writeI32(size);
            for (Object element : instance) {
                definition = context.getClassDefinition(element == null ? void.class : element.getClass());
                protocol.writeI32(definition.getCode());
                ThriftConverter converter = context.getProtocolConverter(definition.getSpecification());
                converter.writeValueTo(context, definition.getType(), definition, element);
            }
        } else {
            mark = EXPLICIT_MARK;
            protocol.writeByte(mark);
            int size = instance.size();
            protocol.writeI32(size);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type elementType = types[0];
            ThriftConverter converter = context.getProtocolConverter(Specification.getSpecification(elementType));
            definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
            for (Object element : instance) {
                converter.writeValueTo(context, elementType, definition, element);
            }
        }
    }

}
