package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 数组转换器
 * 
 * @author Birdy
 *
 */
public class ArrayConverter extends ThriftConverter<Object> {

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Object instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            int size = protocol.readListBegin().size;
            Class<?> clazz = TypeUtility.getRawType(type, null);
            clazz = clazz.getComponentType();
            Specification specification = Specification.getSpecification(clazz);
            ThriftConverter converter = context.getThriftConverter(specification);
            definition = context.getClassDefinition(clazz);
            instance = Array.newInstance(clazz, size);
            for (int index = 0; index < size; index++) {
                Object element = converter.readValueFrom(context, clazz, definition);
                Array.set(instance, index, element);
            }
            protocol.readListEnd();
        }
        protocol.readFieldEnd();
        protocol.readFieldBegin();
        protocol.readStructEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.LIST, (short) 2));
            int size = Array.getLength(instance);
            Class<?> clazz = TypeUtility.getRawType(type, null);
            clazz = clazz.getComponentType();
            Specification specification = Specification.getSpecification(clazz);
            ThriftConverter converter = context.getThriftConverter(specification);
            definition = context.getClassDefinition(clazz);
            protocol.writeListBegin(new TList(TType.STRUCT, size));
            for (int index = 0; index < size; index++) {
                Object element = Array.get(instance, index);
                converter.writeValueTo(context, clazz, definition, element);
            }
            protocol.writeListEnd();
            protocol.writeFieldEnd();
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
