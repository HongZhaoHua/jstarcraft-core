package com.jstarcraft.core.codec.kryo;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.specification.CodecSpecification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Kryo格式编解码器
 * 
 * @author Birdy
 */
public class KryoContentCodec implements ContentCodec {

	private static final Logger LOGGER = LoggerFactory.getLogger(KryoContentCodec.class);

	private final ThreadLocal<Type> currentTypes = new ThreadLocal<>();

	private final CodecDefinition codecDefinition;

	private final Kryo kryo;

	public KryoContentCodec(CodecDefinition definition) {
		this(5, definition);
	}

	public KryoContentCodec(int dimension, CodecDefinition definition) {
		this.codecDefinition = definition;
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		kryo.setRegistrationRequired(true);
		for (ClassDefinition classDefinition : definition.getClassDefinitions()) {
			Class<?> clazz = classDefinition.getType();
			if (clazz == void.class || clazz == Void.class) {
				// TODO
				continue;
			}
			kryo.register(clazz);
			if (clazz.isPrimitive()) {
				for (int index = 0; index < dimension; index++) {
					Object array = Array.newInstance(clazz, 0);
					kryo.register(array.getClass());
					clazz = array.getClass();
				}
			} else {
				Type type = clazz;
				for (int index = 0; index < dimension; index++) {
					type = TypeUtility.genericArrayType(type);
					kryo.register(TypeUtility.getRawType(type, null));
				}
			}

		}
		this.kryo = kryo;
	}

	private Type readValueFrom(Iterator<Integer> iterator) {
		Integer code = iterator.next();
		ClassDefinition definition = codecDefinition.getClassDefinition(code);
		if (definition.getType() == Class.class) {
			code = iterator.next();
			definition = codecDefinition.getClassDefinition(code);
			return definition.getType();
		} else if (definition.getType() == GenericArrayType.class) {
			Type type = currentTypes.get();
			if (type == Class.class) {
				type = readValueFrom(iterator);
				Class<?> clazz = Class.class.cast(type);
				return Array.newInstance(clazz, 0).getClass();
			} else {
				type = readValueFrom(iterator);
				return TypeUtility.genericArrayType(type);
			}
		} else if (definition.getType() == ParameterizedType.class) {
			code = iterator.next();
			definition = codecDefinition.getClassDefinition(code);
			Integer length = iterator.next();
			Type[] types = new Type[length];
			for (int index = 0; index < length; index++) {
				types[index] = readValueFrom(iterator);
			}
			return TypeUtility.parameterize(definition.getType(), types);
		} else {
			throw new CodecConvertionException();
		}
	}

	@Override
	public Object decode(Type type, byte[] content) {
		if (content.length == 0) {
			return null;
		}
		try (Input byteBufferInput = new Input(content)) {
			CodecSpecification specification = CodecSpecification.getSpecification(type);
			if (specification == CodecSpecification.TYPE) {
				currentTypes.set(type);
				LinkedList<Integer> list = kryo.readObject(byteBufferInput, LinkedList.class);
				Type value = readValueFrom(list.iterator());
				currentTypes.remove();
				return value;
			} else {
				if (kryo.isRegistrationRequired()) {
					// Registration registration =
					// kryo.readClass(byteBufferInput);
					return kryo.readObject(byteBufferInput, TypeUtility.getRawType(type, null));
				} else {
					return kryo.readClassAndObject(byteBufferInput);
				}
			}
		} catch (Exception exception) {
			String message = "Kryo解码异常";
			LOGGER.error(message, exception);
			throw new CodecException(message, exception);
		}
	}

	@Override
	public Object decode(Type type, InputStream stream) {
		try (Input byteBufferInput = new Input(stream)) {
			if (stream.available() == 0) {
				return null;
			}
			CodecSpecification specification = CodecSpecification.getSpecification(type);
			if (specification == CodecSpecification.TYPE) {
				currentTypes.set(type);
				LinkedList<Integer> list = kryo.readObject(byteBufferInput, LinkedList.class);
				Type value = readValueFrom(list.iterator());
				currentTypes.remove();
				return value;
			} else {
				if (kryo.isRegistrationRequired()) {
					// Registration registration =
					// kryo.readClass(byteBufferInput);
					return kryo.readObject(byteBufferInput, TypeUtility.getRawType(type, null));
				} else {
					return kryo.readClassAndObject(byteBufferInput);
				}
			}
		} catch (Exception exception) {
			String message = "Kryo解码异常";
			LOGGER.error(message, exception);
			throw new CodecException(message, exception);
		}
	}

	private void writeValueTo(LinkedList<Integer> out, Type value) {
		if (TypeUtility.isInstance(value, Class.class)) {
			Class<?> clazz = TypeUtility.getRawType(value, null);
			if (clazz.isArray()) {
				ClassDefinition definition = codecDefinition.getClassDefinition(GenericArrayType.class);
				out.add(definition.getCode());
				value = TypeUtility.getArrayComponentType(value);
				writeValueTo(out, value);
			} else {
				ClassDefinition definition = codecDefinition.getClassDefinition(Class.class);
				out.add(definition.getCode());
				definition = codecDefinition.getClassDefinition(clazz);
				out.add(definition.getCode());
			}
		} else if (TypeUtility.isInstance(value, GenericArrayType.class)) {
			ClassDefinition definition = codecDefinition.getClassDefinition(GenericArrayType.class);
			out.add(definition.getCode());
			value = TypeUtility.getArrayComponentType(value);
			writeValueTo(out, value);
		} else if (TypeUtility.isInstance(value, ParameterizedType.class)) {
			ClassDefinition definition = codecDefinition.getClassDefinition(ParameterizedType.class);
			out.add(definition.getCode());
			Class<?> clazz = TypeUtility.getRawType(value, null);
			definition = codecDefinition.getClassDefinition(clazz);
			out.add(definition.getCode());
			ParameterizedType parameterizedType = (ParameterizedType) value;
			Type[] types = parameterizedType.getActualTypeArguments();
			out.add(types.length);
			for (int index = 0; index < types.length; index++) {
				writeValueTo(out, types[index]);
			}
		} else {
			throw new CodecConvertionException();
		}
	}

	@Override
	public byte[] encode(Type type, Object content) {
		if (content == null) {
			return new byte[] {};
		}
		try (Output byteBufferOutput = new Output(1024, -1)) {
			CodecSpecification specification = CodecSpecification.getSpecification(type);
			if (specification == CodecSpecification.TYPE) {
				currentTypes.set(type);
				LinkedList<Integer> list = new LinkedList<>();
				writeValueTo(list, (Type) content);
				kryo.writeObject(byteBufferOutput, list);
				byte[] value = byteBufferOutput.toBytes();
				currentTypes.remove();
				return value;
			} else {
				if (kryo.isRegistrationRequired()) {
					// kryo.writeClass(byteBufferOutput, instance.getClass());
					kryo.writeObject(byteBufferOutput, content);
					return byteBufferOutput.toBytes();
				} else {
					kryo.writeClassAndObject(byteBufferOutput, content);
					return byteBufferOutput.toBytes();
				}
			}
		} catch (Exception exception) {
			String message = "Kryo编码异常";
			LOGGER.error(message, exception);
			throw new CodecException(message, exception);
		}
	}

	@Override
	public void encode(Type type, Object content, OutputStream stream) {
		try (Output byteBufferOutput = new Output(stream)) {
			if (content == null) {
				return;
			}
			CodecSpecification specification = CodecSpecification.getSpecification(type);
			if (specification == CodecSpecification.TYPE) {
				currentTypes.set(type);
				LinkedList<Integer> list = new LinkedList<>();
				writeValueTo(list, (Type) content);
				kryo.writeObject(byteBufferOutput, list);
				currentTypes.remove();
			} else {
				if (kryo.isRegistrationRequired()) {
					kryo.writeObject(byteBufferOutput, content);
				} else {
					kryo.writeClassAndObject(byteBufferOutput, content);
				}
			}
		} catch (Exception exception) {
			String message = "Kryo编码异常";
			LOGGER.error(message, exception);
			throw new CodecException(message, exception);
		}
	}

}
