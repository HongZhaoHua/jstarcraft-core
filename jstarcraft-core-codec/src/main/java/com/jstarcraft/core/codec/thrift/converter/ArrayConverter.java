package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Optional;

/**
 * 数组转换器
 * 
 * @author Birdy
 *
 */
public class ArrayConverter extends ProtocolConverter<Object> {

    @Override
    public Object readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws Exception {
        org.apache.thrift.protocol.TList _list0 = protocol.readListBegin();
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = clazz.getComponentType();
        Object value = Array.newInstance(clazz, _list0.size);
        Specification specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
        ProtocolConverter converter = context.getProtocolConverter(specification);
        for (int index = 0; index < _list0.size; index++) {
            Object object = converter.readValueFrom(context, clazz, definition);
            Array.set(value, index, object);
        }
        protocol.readListEnd();
        return value;
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Object value) throws Exception {
        int length= value==null?0:Array.getLength(value);
        protocol.writeListBegin(new org.apache.thrift.protocol.TList(context.getThriftType(type),length));
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz=clazz.getComponentType();
        Specification specification;
        if(clazz==null){
            specification=definition.getSpecification();
        }else{
            specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
        }
        ProtocolConverter converter = context.getProtocolConverter(specification);
        for (int index = 0; index < length; index++) {
            Object object = Array.get(value, index);
            definition = context.getClassDefinition(object == null ? clazz : object.getClass());
            converter.writeValueTo(context, definition.getType(), definition, object);
        }
        protocol.writeListEnd();
    }

}
