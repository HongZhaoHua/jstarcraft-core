package com.jstarcraft.core.codec.protocolbufferx.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.protocolbufferx.ProtocolReader;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * Void转换器
 * 
 * @author Birdy
 *
 */
public class VoidConverter extends BinaryConverter<Object> {

	@Override
	public Object readValueFrom(ProtocolReader context, Type type, ClassDefinition definition) throws IOException {
		return null;
	}

	@Override
	public void writeValueTo(ProtocolWriter context, Type type, ClassDefinition definition, Object value) throws IOException {
	}

}
