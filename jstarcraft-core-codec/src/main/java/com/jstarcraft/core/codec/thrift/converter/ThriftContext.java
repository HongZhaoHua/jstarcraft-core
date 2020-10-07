package com.jstarcraft.core.codec.thrift.converter;

import java.util.EnumMap;

import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;

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

    /** 协议定义 */
    private final CodecDefinition definition;

    private final TProtocol protocol;

    public ThriftContext(CodecDefinition definition, TProtocol protocol) {
        this.definition = definition;
        this.protocol = protocol;
    }

    public ThriftConverter getThriftConverter(Specification specification) {
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

}
