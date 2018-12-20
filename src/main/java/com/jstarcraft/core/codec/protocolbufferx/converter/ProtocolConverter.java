package com.jstarcraft.core.codec.protocolbufferx.converter;

import java.lang.reflect.Type;

import com.jstarcraft.core.codec.protocolbufferx.ProtocolReader;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 协议转换器
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface ProtocolConverter<T> {

	/**
	 * 从指定上下文读取内容
	 * 
	 * @param context
	 * @param information
	 * @return
	 */
	T readValueFrom(ProtocolReader context, Type type, ClassDefinition definition) throws Exception;

	/**
	 * 将指定内容写到上下文
	 * 
	 * @param context
	 * @param value
	 * @throws Exception
	 */
	void writeValueTo(ProtocolWriter context, Type type, ClassDefinition definition, T value) throws Exception;

}