package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;

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
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        TList list = protocol.readListBegin();
        int length = list.size;
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = clazz.getComponentType();
        Object value = Array.newInstance(clazz, length);
        Specification specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
        ThriftConverter converter = context.getProtocolConverter(specification);
        for (int index = 0; index < length; index++) {
            Object object = converter.readValueFrom(context, clazz, definition);
            Array.set(value, index, object);
        }
        protocol.readListEnd();
        return value;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws Exception {
        TProtocol protocol = context.getProtocol();
        int length = value == null ? 0 : Array.getLength(value);
        protocol.writeListBegin(new TList(context.getThriftType(type), length));
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = clazz.getComponentType();
        Specification specification;
        if (clazz == null) {
            specification = definition.getSpecification();
        } else {
            specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
        }
        ThriftConverter converter = context.getProtocolConverter(specification);
        for (int index = 0; index < length; index++) {
            Object object = Array.get(value, index);
            definition = context.getClassDefinition(object == null ? clazz : object.getClass());
            converter.writeValueTo(context, definition.getType(), definition, object);
        }
        protocol.writeListEnd();
    }

}
