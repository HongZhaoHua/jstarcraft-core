package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;

import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 协议转换器
 * 
 * @author Birdy
 *
 * @param <T>
 */
public abstract class ThriftConverter<T> {

    /**
     * 从指定上下文读取内容
     * 
     * @param context
     * @param type
     * @param definition
     * @throws Exception
     * @return
     */
    abstract public T readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception;

    /**
     * 将指定内容写到上下文
     * 
     * @param context
     * @param value
     * @throws Exception
     */
    abstract public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, T value) throws Exception;

}