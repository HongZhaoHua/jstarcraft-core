package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 类型转换器
 * 
 * @author Birdy
 *
 */
public class TypeConverter extends ThriftConverter<Type> {

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

    @Override
    public byte getThriftType(Type type) {
        return TType.STRUCT;
    }

    @Override
    public Type readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Type instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            if (feild.id == 2) {
                boolean generic = protocol.readBool();
                protocol.readFieldEnd();
                protocol.readFieldBegin();
                definition = context.getClassDefinition(Type.class);
                Type node = (Type) readValueFrom(context, Type.class, definition);
                protocol.readFieldEnd();
                if (generic) {
                    instance = TypeUtility.genericArrayType(node);
                } else {
                    instance = Array.newInstance(TypeUtility.getRawType(node, null), 0).getClass();
                }
            } else {
                int code = protocol.readI32();
                protocol.readFieldEnd();
                protocol.readFieldBegin();
                Specification specification = Specification.getSpecification(Type[].class);
                ThriftConverter converter = context.getProtocolConverter(specification);
                definition = context.getClassDefinition(Type[].class);
                Type[] types = (Type[]) converter.readValueFrom(context, Type[].class, definition);
                protocol.readFieldEnd();
                if (types == null) {
                    definition = context.getClassDefinition(code);
                    instance = definition.getType();
                } else {
                    definition = context.getClassDefinition(code);
                    instance = TypeUtility.parameterize(definition.getType(), types);
                }
            }
        }
        protocol.readFieldBegin();
        protocol.readStructEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Type instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            Type node = TypeUtility.getArrayComponentType(instance);
            if (node != null) {
                boolean generic = instance instanceof GenericArrayType;
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.STRUCT, (short) 2));
                definition = context.getClassDefinition(Type.class);
                protocol.writeBool(generic);
                protocol.writeFieldEnd();
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.STRUCT, (short) 3));
                definition = context.getClassDefinition(Type.class);
                writeValueTo(context, node.getClass(), definition, node);
                protocol.writeFieldEnd();
            } else {
                Class<?> clazz = TypeUtility.getRawType(instance, null);
                definition = context.getClassDefinition(clazz);
                int code = definition.getCode();
                Type[] types;
                if (instance instanceof ParameterizedType) {
                    code = definition.getCode();
                    ParameterizedType parameterizedType = (ParameterizedType) instance;
                    types = parameterizedType.getActualTypeArguments();
                } else {
                    types = null;
                }
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.I32, (short) 4));
                protocol.writeI32(code);
                protocol.writeFieldEnd();
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.STRUCT, (short) 5));
                Specification specification = Specification.getSpecification(Type[].class);
                ThriftConverter converter = context.getProtocolConverter(specification);
                definition = context.getClassDefinition(Type[].class);
                converter.writeValueTo(context, Type[].class, definition, types);
                protocol.writeFieldEnd();
            }
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
