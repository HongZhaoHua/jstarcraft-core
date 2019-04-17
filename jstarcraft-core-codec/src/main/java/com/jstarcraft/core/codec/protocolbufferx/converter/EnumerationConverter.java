package com.jstarcraft.core.codec.protocolbufferx.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.protocolbufferx.ProtocolReader;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 枚举转换器
 * 
 * @author Birdy
 *
 */
public class EnumerationConverter extends BinaryConverter<Object> {

	/** 0000 0000(Null标记) */
	private static final byte NULL_MARK = (byte) 0x00;

	@Override
	public Object readValueFrom(ProtocolReader context, Type type, ClassDefinition definition) throws IOException {
		InputStream in = context.getInputStream();
		int ordinal = NumberConverter.readNumber(in).intValue();
		if (ordinal == NULL_MARK) {
			return null;
		}
		// byte information = (byte) in.read();
		// int code = NumberConverter.readNumber(in).intValue();

		Class<?> clazz = definition.getType();
		return clazz.getEnumConstants()[ordinal - 1];
	}

	@Override
	public void writeValueTo(ProtocolWriter context, Type type, ClassDefinition definition, Object value) throws IOException {
		OutputStream out = context.getOutputStream();
		if (value == null) {
			out.write(NULL_MARK);
			return;
		}
		// byte information = CodecSpecification.ENUMERATION.getCode();
		// out.write(information);
		Enum<?> enumeration = (Enum<?>) value;
		// Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>)
		// enumeration.getClass();

		// int code = definition.getCode();
		int ordinal = enumeration.ordinal() + 1;
		// NumberConverter.writeNumber(out, code);
		NumberConverter.writeNumber(out, ordinal);
	}

}
