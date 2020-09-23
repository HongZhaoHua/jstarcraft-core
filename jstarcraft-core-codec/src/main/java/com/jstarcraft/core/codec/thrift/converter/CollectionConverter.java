package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.converter.NumberConverter;
import com.jstarcraft.core.codec.standard.converter.StandardConverter;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TType;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

/**
 * 集合转换器
 * 
 * @author Birdy
 *
 */
public class CollectionConverter extends ProtocolConverter<Collection<?>> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    /** 0000 0003(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x03;

    @Override
    public Collection<?> readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws Exception {
        byte information = protocol.readByte();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = protocol.readI32();
            Collection collection = (Collection) definition.getInstance();
            context.putCollectionValue(collection);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type elementType = types[0];
            ProtocolConverter converter = context.getProtocolConverter(Specification.getSpecification(elementType));
            definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
            for (int index = 0; index < size; index++) {
                Object object = converter.readValueFrom(context, elementType, definition);
                collection.add(object);
            }
            return collection;
        } else if (mark == IMPLICIT_MARK) {
            int size = protocol.readI32();
            Collection collection = (Collection) definition.getInstance();
            context.putCollectionValue(collection);
            for (int index = 0; index < size; index++) {
                int code = protocol.readI32();
                definition = context.getClassDefinition(code);
                ProtocolConverter converter = context.getProtocolConverter(definition.getSpecification());
                Object object = converter.readValueFrom(context, definition.getType(), definition);
                collection.add(object);
            }
            return collection;
        } else if (mark == REFERENCE_MARK) {
            int reference = protocol.readI32();
            Collection collection = (Collection) context.getCollectionValue(reference);
            return collection;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Collection<?> value) throws Exception {
        byte information = ClassDefinition.getMark(Specification.COLLECTION);
        if (value == null) {
            protocol.writeByte(information);
            return;
        }
        int reference = context.getCollectionIndex(value);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            protocol.writeByte(information);
            protocol.writeI32(reference);
        } else {
            if (type instanceof Class) {
                information |= IMPLICIT_MARK;
                context.putCollectionValue(value);
                protocol.writeByte(information);
                int size = value.size();
                protocol.writeI32(size);
                for (Object object : value) {
                    definition = context.getClassDefinition(object == null ? void.class : object.getClass());
                    protocol.writeI32(definition.getCode());
                    ProtocolConverter converter = context.getProtocolConverter(definition.getSpecification());
                    converter.writeValueTo(context, definition.getType(), definition, object);
                }
            } else {
                information |= EXPLICIT_MARK;
                context.putCollectionValue(value);
                protocol.writeByte(information);
                int size = value.size();
                protocol.writeI32(size);
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                Type elementType = types[0];
                ProtocolConverter converter = context.getProtocolConverter(Specification.getSpecification(elementType));
                definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
                for (Object object : value) {
                    converter.writeValueTo(context, elementType, definition, object);
                }
            }
        }
    }

}
