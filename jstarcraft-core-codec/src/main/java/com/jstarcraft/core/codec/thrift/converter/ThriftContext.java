package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 协议上下文
 * 
 * @author Birdy
 *
 */
public class ThriftContext {

    protected static final EnumMap<Specification, ThriftConverter<?>> converters = new EnumMap<>(Specification.class);

    static {
        converters.put(Specification.ARRAY, new ArrayConverter());
        converters.put(Specification.BOOLEAN, new BooleanConverter());
        converters.put(Specification.COLLECTION, new CollectionConverter());
        converters.put(Specification.ENUMERATION, new EnumerationConverter());
        converters.put(Specification.MAP, new MapConverter());
        converters.put(Specification.NUMBER, new NumberConverter());
        converters.put(Specification.OBJECT, new ObjectConverter());
        converters.put(Specification.STRING, new StringConverter());
        converters.put(Specification.INSTANT, new InstantConverter());
        converters.put(Specification.TYPE, new TypeConverter());
        converters.put(Specification.VOID, new VoidConverter());
    }

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

    /** 协议定义 */
    private final CodecDefinition definition;

    private final TProtocol protocol;

    public ThriftContext(CodecDefinition definition, TProtocol protocol) {
        this.definition = definition;
        this.protocol = protocol;
    }

    public ThriftConverter getProtocolConverter(Specification specification) {
        ThriftConverter converter = converters.get(specification);
        return converter;
    }

    protected ClassDefinition getClassDefinition(int index) {
        return definition.getClassDefinition(index);
    }

    protected ClassDefinition getClassDefinition(Class<?> clazz) {
        return definition.getClassDefinition(clazz);
    }

    public TProtocol getProtocol() {
        return protocol;
    }

    @Deprecated
    // TODO 考虑重构
    public byte getThriftType(Type type) {
        Class<?> clazz = TypeUtility.getRawType(type, null);
        if (clazz.isArray()) {
            return TType.LIST;
        }
        if (type.getClass().getClassLoader() == null) {
            return TType.STRUCT;
        }
        if (!typeReference.containsKey(type)) {
            throw new CodecConvertionException();
        }
        return typeReference.get(type);
    }

}
