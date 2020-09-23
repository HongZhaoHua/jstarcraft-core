package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReference;
import com.jstarcraft.core.common.reflection.Specification;
import org.apache.thrift.protocol.TProtocol;

import java.util.EnumMap;

/**
 * 协议上下文
 * 
 * @author Birdy
 *
 */
public abstract class ProtocolContext {

    protected static final EnumMap<Specification, ProtocolConverter<?>> converters = new EnumMap<>(Specification.class);

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
        //暂时无用,且无相关key
        //converters.put(Specification.OBJECT, new ExceptionConverter());
        //converters.put(Specification.OBJECT, new ServiceConverter());
    }

    /** 协议定义 */
    private final CodecDefinition definition;

    protected TProtocol protocol;

    /** 读写上下文过程的数组引用 */
    protected ThriftReference<Object> arrayReference = new ThriftReference<Object>();
    /** 读写上下文过程的集合引用 */
    protected ThriftReference<Object> collectionReference = new ThriftReference<Object>();
    /** 读写上下文过程的映射引用 */
    protected ThriftReference<Object> mapReference = new ThriftReference<Object>();
    /** 读写上下文过程的对象引用 */
    protected ThriftReference<Object> objectReference = new ThriftReference<Object>();
    /** 读写上下文过程的字符串引用 */
    protected ThriftReference<String> stringReference = new ThriftReference<String>();

    public ProtocolContext(CodecDefinition definition) {
        this.definition = definition;
    }

    public ProtocolConverter getProtocolConverter(Specification specification) {
        ProtocolConverter converter = converters.get(specification);
        converter.setProtocol(protocol);
        return converter;
    }

    protected ClassDefinition getClassDefinition(int index) {
        return definition.getClassDefinition(index);
    }

    protected ClassDefinition getClassDefinition(Class<?> clazz) {
        return definition.getClassDefinition(clazz);
    }

    protected Object getArrayValue(int index) {
        return arrayReference.getValue(index);
    }

    protected int getArrayIndex(Object value) {
        return arrayReference.getIndex(value);
    }

    protected int putArrayValue(Object value) {
        return arrayReference.putValue(value);
    }

    protected Object getCollectionValue(int index) {
        return collectionReference.getValue(index);
    }

    protected int getCollectionIndex(Object value) {
        return collectionReference.getIndex(value);
    }

    protected int putCollectionValue(Object value) {
        return collectionReference.putValue(value);
    }

    protected Object getMapValue(int index) {
        return mapReference.getValue(index);
    }

    protected int getMapIndex(Object value) {
        return mapReference.getIndex(value);
    }

    protected int putMapValue(Object value) {
        return mapReference.putValue(value);
    }

    protected Object getObjectValue(int index) {
        return objectReference.getValue(index);
    }

    protected int getObjectIndex(Object value) {
        return objectReference.getIndex(value);
    }

    protected int putObjectValue(Object value) {
        return objectReference.putValue(value);
    }

    protected String getStringValue(int index) {
        return stringReference.getValue(index);
    }

    protected int getStringIndex(String value) {
        return stringReference.getIndex(value);
    }

    protected int putStringValue(String value) {
        return stringReference.putValue(value);
    }

}
