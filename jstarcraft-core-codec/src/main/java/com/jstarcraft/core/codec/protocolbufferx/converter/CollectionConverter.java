package com.jstarcraft.core.codec.protocolbufferx.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.jstarcraft.core.codec.protocolbufferx.ProtocolReader;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolWriter;
import com.jstarcraft.core.codec.protocolbufferx.exception.ProtocolConverterException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecSpecification;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.core.utility.TypeUtility;

/**
 * 集合转换器
 * 
 * @author Birdy
 *
 */
public class CollectionConverter extends BinaryConverter<Collection<?>> {

	/** 0000 0000(Null标记) */
	private static final byte NULL_MARK = (byte) 0x00;

	/** 0000 0001(显式标记) */
	private static final byte EXPLICIT_MARK = (byte) 0x01;

	/** 0000 0002(隐式标记) */
	private static final byte IMPLICIT_MARK = (byte) 0x02;

	/** 0000 0003(引用标记) */
	private static final byte REFERENCE_MARK = (byte) 0x03;

	@Override
	public Collection<?> readValueFrom(ProtocolReader context, Type type, ClassDefinition definition) throws Exception {
		InputStream in = context.getInputStream();
		byte information = (byte) in.read();
		byte mark = getMark(information);
		if (mark == NULL_MARK) {
			return null;
		}
		if (mark == EXPLICIT_MARK) {
			int size = NumberConverter.readNumber(in).intValue();
//			int code = NumberConverter.readNumber(in).intValue();
//			definition = context.getClassDefinition(code);
			Collection collection = (Collection) definition.getInstance();
			context.putCollectionValue(collection);
//			ProtocolConverter converter = context.getProtocolConverter(CodecSpecification.TYPE);
//			Type elementType = (Type) converter.readValueFrom(context, Type.class, null);
			ParameterizedType parameterizedType = (ParameterizedType)type;
			Type[] types = parameterizedType.getActualTypeArguments();
			Type elementType = types[0];
			ProtocolConverter converter = context.getProtocolConverter(CodecSpecification.getSpecification(elementType));
			definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
			for (int index = 0; index < size; index++) {
				Object object = converter.readValueFrom(context, elementType, definition);
				collection.add(object);
			}
			return collection;
		} else if (mark == IMPLICIT_MARK) {
			int size = NumberConverter.readNumber(in).intValue();
//			int code = NumberConverter.readNumber(in).intValue();
//			definition = context.getClassDefinition(code);
			Collection collection = (Collection) definition.getInstance();
			context.putCollectionValue(collection);
			for (int index = 0; index < size; index++) {
				int code = NumberConverter.readNumber(in).intValue();
				definition = context.getClassDefinition(code);
				ProtocolConverter converter = context.getProtocolConverter(definition.getSpecification());
				Object object = converter.readValueFrom(context, definition.getType(), definition);
				collection.add(object);
			}
			return collection;
		} else if (mark == REFERENCE_MARK) {
			int reference = NumberConverter.readNumber(in).intValue();
			Collection collection = (Collection) context.getCollectionValue(reference);
			return collection;
		}
		String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
		throw new ProtocolConverterException(message);
	}

	@Override
	public void writeValueTo(ProtocolWriter context, Type type, ClassDefinition definition, Collection<?> value) throws Exception {
		OutputStream out = context.getOutputStream();
		byte information = CodecSpecification.COLLECTION.getCode();
		if (value == null) {
			out.write(information);
			return;
		}
		int reference = context.getCollectionIndex(value);
		if (reference != -1) {
			information |= REFERENCE_MARK;
			out.write(information);
			NumberConverter.writeNumber(out, reference);
		} else {
			if (type instanceof Class) {
				information |= IMPLICIT_MARK;
				context.putCollectionValue(value);
				out.write(information);
				int size = value.size();
				NumberConverter.writeNumber(out, size);
//				int code = definition.getCode();
//				NumberConverter.writeNumber(out, code);
				for (Object object : value) {
					definition = context.getClassDefinition(object == null ? void.class : object.getClass());
					NumberConverter.writeNumber(out, definition.getCode());
					ProtocolConverter converter = context.getProtocolConverter(definition.getSpecification());
					converter.writeValueTo(context, definition.getType(), definition, object);
				}
			} else {
				information |= EXPLICIT_MARK;
				context.putCollectionValue(value);
				out.write(information);
				int size = value.size();
				NumberConverter.writeNumber(out, size);
//				int code = definition.getCode();
//				NumberConverter.writeNumber(out, code);
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] types = parameterizedType.getActualTypeArguments();
				Type elementType = types[0];
//				ProtocolConverter converter = context.getProtocolConverter(CodecSpecification.TYPE);
//				converter.writeValueTo(context, Type.class, null, elementType);
				ProtocolConverter converter = context.getProtocolConverter(CodecSpecification.getSpecification(elementType));
				definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
				for (Object object : value) {
					converter.writeValueTo(context, elementType, definition, object);
				}
			}
		}
	}

}
