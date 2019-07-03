package com.jstarcraft.core.codec.protocolbufferx;

import java.util.EnumMap;

import com.jstarcraft.core.codec.protocolbufferx.converter.ArrayConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.BooleanConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.CollectionConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.EnumerationConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.InstantConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.MapConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.NumberConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.ObjectConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.ProtocolConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.StringConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.TypeConverter;
import com.jstarcraft.core.codec.protocolbufferx.converter.VoidConverter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;

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
    }

    /** 协议定义 */
    private final CodecDefinition definition;

    /** 读写上下文过程的数组引用 */
    protected ProtocolReference<Object> arrayReference = new ProtocolReference<Object>();
    /** 读写上下文过程的集合引用 */
    protected ProtocolReference<Object> collectionReference = new ProtocolReference<Object>();
    /** 读写上下文过程的映射引用 */
    protected ProtocolReference<Object> mapReference = new ProtocolReference<Object>();
    /** 读写上下文过程的对象引用 */
    protected ProtocolReference<Object> objectReference = new ProtocolReference<Object>();
    /** 读写上下文过程的字符串引用 */
    protected ProtocolReference<String> stringReference = new ProtocolReference<String>();

    public ProtocolContext(CodecDefinition definition) {
        this.definition = definition;
    }

    public ProtocolConverter getProtocolConverter(Specification specification) {
        ProtocolConverter converter = converters.get(specification);
        return converter;
    }

    public ClassDefinition getClassDefinition(int index) {
        return definition.getClassDefinition(index);
    }

    public ClassDefinition getClassDefinition(Class<?> clazz) {
        return definition.getClassDefinition(clazz);
    }

    public Object getArrayValue(int index) {
        return arrayReference.getValue(index);
    }

    public int getArrayIndex(Object value) {
        return arrayReference.getIndex(value);
    }

    public int putArrayValue(Object value) {
        return arrayReference.putValue(value);
    }

    public Object getCollectionValue(int index) {
        return collectionReference.getValue(index);
    }

    public int getCollectionIndex(Object value) {
        return collectionReference.getIndex(value);
    }

    public int putCollectionValue(Object value) {
        return collectionReference.putValue(value);
    }

    public Object getMapValue(int index) {
        return mapReference.getValue(index);
    }

    public int getMapIndex(Object value) {
        return mapReference.getIndex(value);
    }

    public int putMapValue(Object value) {
        return mapReference.putValue(value);
    }

    public Object getObjectValue(int index) {
        return objectReference.getValue(index);
    }

    public int getObjectIndex(Object value) {
        return objectReference.getIndex(value);
    }

    public int putObjectValue(Object value) {
        return objectReference.putValue(value);
    }

    public String getStringValue(int index) {
        return stringReference.getValue(index);
    }

    public int getStringIndex(String value) {
        return stringReference.getIndex(value);
    }

    public int putStringValue(String value) {
        return stringReference.putValue(value);
    }

}
