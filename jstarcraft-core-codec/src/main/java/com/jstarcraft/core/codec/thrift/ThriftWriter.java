package com.jstarcraft.core.codec.thrift;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.thrift.converter.ProtocolContext;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 协议写出器
 * 
 * <pre>
 * 每次编码都必须使用
 * </pre>
 * 
 * @author Birdy
 */
public class ThriftWriter extends ProtocolContext {

    private static Map<Class, Byte> typeReference = new HashMap<>();

    static {
        // typeReference.put(STOP.class, TType.STOP);
        typeReference.put(Void.class, TType.VOID);
        typeReference.put(void.class, TType.VOID);
        typeReference.put(Boolean.class, TType.BOOL);
        typeReference.put(boolean.class, TType.BOOL);
        typeReference.put(Byte.class, TType.BYTE);
        typeReference.put(byte.class, TType.BYTE);
        typeReference.put(Double.class, TType.DOUBLE);
        typeReference.put(double.class, TType.DOUBLE);
        typeReference.put(Short.class, TType.I16);
        typeReference.put(short.class, TType.I16);
        typeReference.put(Integer.class, TType.I32);
        typeReference.put(int.class, TType.I32);
        typeReference.put(Long.class, TType.I64);
        typeReference.put(long.class, TType.I64);
        typeReference.put(String.class, TType.STRING);
        // typeReference.put(Struct.class,TType.STRUCT);
        typeReference.put(Map.class, TType.MAP);
        typeReference.put(Set.class, TType.SET);
        typeReference.put(List.class, TType.LIST);
        typeReference.put(Collection.class, TType.LIST);
        typeReference.put(Object.class, TType.STRUCT);
        typeReference.put(Enum.class, TType.ENUM);
    }

    public byte getThriftType(Type type) {
        Class<?> clazz = TypeUtility.getRawType(type, null);
        if (clazz.isArray()) {
            return TType.LIST;
        }
        if (type.getClass().getClassLoader() == null) {
            return TType.STRUCT;
        }
        if (!typeReference.containsKey(type)) {
            throw new CodecConvertionException("未定义的java类型:" + type);
        }
        return typeReference.get(type);
    }

    public ThriftWriter(CodecDefinition definition, TProtocol tProtocol) {
        super(definition);
        super.protocol = tProtocol;
    }

}
