package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 数组转换器
 * 
 * @author Birdy
 *
 */
public class ArrayConverter extends ThriftConverter<Object> {

    @Override
    public byte getThriftType(Type type) {
        return TType.LIST;
    }

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        int length = protocol.readListBegin().size;
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = clazz.getComponentType();
        Specification specification = Specification.getSpecification(clazz);
        ThriftConverter converter = context.getProtocolConverter(specification);
        definition = context.getClassDefinition(clazz);
        Object instance = Array.newInstance(clazz, length);
        for (int index = 0; index < length; index++) {
            Object element = converter.readValueFrom(context, clazz, definition);
            Array.set(instance, index, element);
        }
        protocol.readListEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        int length = instance == null ? 0 : Array.getLength(instance);
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = clazz.getComponentType();
        Specification specification = Specification.getSpecification(clazz);
        ThriftConverter converter = context.getProtocolConverter(specification);
        definition = context.getClassDefinition(clazz);
        protocol.writeListBegin(new TList(converter.getThriftType(clazz), length));
        for (int index = 0; index < length; index++) {
            Object element = Array.get(instance, index);
            converter.writeValueTo(context, clazz, definition, element);
        }
        protocol.writeListEnd();
    }

}
