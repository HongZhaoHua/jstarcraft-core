package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 映射转换器
 * 
 * @author Birdy
 *
 */
public class MapConverter extends ThriftConverter<Map<Object, Object>> {

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

    @Override
    public Map<Object, Object> readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Map instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            instance = (Map) definition.getInstance();
            int size = protocol.readMapBegin().size;
            // 兼容UniMi
            type = TypeUtility.refineType(type, Map.class);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type keyType = types[0];
            Type valueType = types[1];
            ThriftConverter keyConverter = context.getThriftConverter(Specification.getSpecification(keyType));
            ThriftConverter valueConverter = context.getThriftConverter(Specification.getSpecification(valueType));
            ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
            ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
            for (int index = 0; index < size; index++) {
                Object key = keyConverter.readValueFrom(context, keyType, keyDefinition);
                Object value = valueConverter.readValueFrom(context, valueType, valueDefinition);
                instance.put(key, value);
            }
            protocol.readMapEnd();
        }
        protocol.readFieldEnd();
        protocol.readFieldBegin();
        protocol.readStructEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Map<Object, Object> instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.LIST, (short) 2));
            int size = instance.size();
            // 兼容UniMi
            type = TypeUtility.refineType(type, Map.class);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type keyType = types[0];
            Type valueType = types[1];
            ThriftConverter keyConverter = context.getThriftConverter(Specification.getSpecification(keyType));
            ThriftConverter valueConverter = context.getThriftConverter(Specification.getSpecification(valueType));
            ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
            ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
            protocol.writeMapBegin(new TMap(TType.STRUCT, TType.STRUCT, size));
            for (Entry<Object, Object> keyValue : instance.entrySet()) {
                keyConverter.writeValueTo(context, keyType, keyDefinition, keyValue.getKey());
                valueConverter.writeValueTo(context, valueType, valueDefinition, keyValue.getValue());
            }
            protocol.writeMapEnd();
            protocol.writeFieldEnd();
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
