package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

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
 * 集合转换器
 * 
 * @author Birdy
 *
 */
public class CollectionConverter extends ThriftConverter<Collection<?>> {

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

   

    @Override
    public Collection<?> readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Collection instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            instance = (Collection) definition.getInstance();
            int size = protocol.readListBegin().size;
            // 兼容UniMi
            type = TypeUtility.refineType(type, Collection.class);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type elementType = types[0];
            ThriftConverter converter = context.getThriftConverter(Specification.getSpecification(elementType));
            definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
            for (int index = 0; index < size; index++) {
                Object element = converter.readValueFrom(context, elementType, definition);
                instance.add(element);
            }
            protocol.readListEnd();
        }
        protocol.readFieldEnd();
        protocol.readFieldBegin();
        protocol.readStructEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Collection<?> instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.LIST, (short) 2));
            int size = instance.size();
            // 兼容UniMi
            type = TypeUtility.refineType(type, Collection.class);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type elementType = types[0];
            ThriftConverter converter = context.getThriftConverter(Specification.getSpecification(elementType));
            definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
            protocol.writeListBegin(new TList(TType.STRUCT, size));
            for (Object element : instance) {
                converter.writeValueTo(context, elementType, definition, element);
            }
            protocol.writeListEnd();
            protocol.writeFieldEnd();
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
