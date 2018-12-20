package com.jstarcraft.core.codec.specification;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.jstarcraft.core.utility.TypeUtility;

/**
 * 编解码规范
 * 
 * @author Birdy
 *
 */
public enum CodecSpecification {

	/** 数组 */
	ARRAY(0x00),
	/** 布尔 */
	BOOLEAN(0x10),
	/** 集合 */
	COLLECTION(0x20),
	/** 枚举 */
	ENUMERATION(0x30),
	/** 时间 */
	INSTANT(0x40),
	/** 映射 */
	MAP(0x50),
	/** 数值 */
	NUMBER(0x60),
	/** 对象 */
	OBJECT(0x70),
	/** 字符串 */
	STRING(0x80),
	/** 类型 */
	TYPE(0x90),
	/** 未知 */
	VOID(0xA0);

	public static final HashMap<Class<?>, CodecSpecification> type2Specifitions = new HashMap<>();
	static {
		// 布尔规范
		type2Specifitions.put(AtomicBoolean.class, CodecSpecification.BOOLEAN);
		type2Specifitions.put(boolean.class, CodecSpecification.BOOLEAN);
		type2Specifitions.put(Boolean.class, CodecSpecification.BOOLEAN);

		// 数值规范
		type2Specifitions.put(AtomicInteger.class, CodecSpecification.NUMBER);
		type2Specifitions.put(AtomicLong.class, CodecSpecification.NUMBER);
		type2Specifitions.put(byte.class, CodecSpecification.NUMBER);
		type2Specifitions.put(short.class, CodecSpecification.NUMBER);
		type2Specifitions.put(int.class, CodecSpecification.NUMBER);
		type2Specifitions.put(long.class, CodecSpecification.NUMBER);
		type2Specifitions.put(float.class, CodecSpecification.NUMBER);
		type2Specifitions.put(double.class, CodecSpecification.NUMBER);
		type2Specifitions.put(Byte.class, CodecSpecification.NUMBER);
		type2Specifitions.put(Short.class, CodecSpecification.NUMBER);
		type2Specifitions.put(Integer.class, CodecSpecification.NUMBER);
		type2Specifitions.put(Long.class, CodecSpecification.NUMBER);
		type2Specifitions.put(Float.class, CodecSpecification.NUMBER);
		type2Specifitions.put(Double.class, CodecSpecification.NUMBER);
		type2Specifitions.put(BigInteger.class, CodecSpecification.NUMBER);
		type2Specifitions.put(BigDecimal.class, CodecSpecification.NUMBER);

		// 字符规范
		type2Specifitions.put(char.class, CodecSpecification.STRING);
		type2Specifitions.put(Character.class, CodecSpecification.STRING);
		type2Specifitions.put(String.class, CodecSpecification.STRING);

		// 时间规范
		type2Specifitions.put(Date.class, CodecSpecification.INSTANT);
		type2Specifitions.put(Instant.class, CodecSpecification.INSTANT);

		// 类型规范
		type2Specifitions.put(Class.class, CodecSpecification.TYPE);
		type2Specifitions.put(GenericArrayType.class, CodecSpecification.TYPE);
		type2Specifitions.put(ParameterizedType.class, CodecSpecification.TYPE);
		type2Specifitions.put(TypeVariable.class, CodecSpecification.TYPE);
		type2Specifitions.put(WildcardType.class, CodecSpecification.TYPE);

		// 未知规范
		type2Specifitions.put(void.class, CodecSpecification.VOID);
		type2Specifitions.put(Void.class, CodecSpecification.VOID);
	}

	/** 类型码 */
	private byte code;

	private CodecSpecification(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	// /**
	// * 根据指定代号获取对应的规范类型
	// *
	// * @param value
	// * @return
	// */
	// public static CodecSpecification getSpecification(byte code) {
	// for (CodecSpecification specification : CodecSpecification.values()) {
	// if (specification.getCode() == code) {
	// return specification;
	// }
	// }
	// String message = StringUtility.format("缺少{}类型的转换器", code);
	// throw new ProtocolDefinitionException(message);
	// }

	// /**
	// * 根据指定实例获取对应的规范类型
	// *
	// * @param instance
	// * @return
	// */
	// public static CodecSpecification getSpecification(Object instance) {
	// if (instance == null) {
	// return CodecSpecification.NULL;
	// }
	// if (instance instanceof Type) {
	// return CodecSpecification.TYPE;
	// }
	// Class<?> clazz = instance.getClass();
	// if (clazz.isArray()) {
	// return CodecSpecification.ARRAY;
	// }
	// if (clazz.isEnum()) {
	// return CodecSpecification.ENUMERATION;
	// }
	// if (Collection.class.isAssignableFrom(clazz)) {
	// return CodecSpecification.COLLECTION;
	// }
	// if (Map.class.isAssignableFrom(clazz)) {
	// return CodecSpecification.MAP;
	// }
	// CodecSpecification specification = type2Specifitions.get(clazz);
	// return specification != null ? specification : CodecSpecification.OBJECT;
	// }

	/**
	 * 根据指定实例获取对应的规范类型
	 * 
	 * @param instance
	 * @return
	 */
	public static CodecSpecification getSpecification(Type type) {
		if (type == null) {
			return CodecSpecification.VOID;
		}
		if (TypeUtility.isAssignable(type, Type.class)) {
			return CodecSpecification.TYPE;
		}
		Class<?> clazz = TypeUtility.getRawType(type, null);
		if (clazz.isArray()) {
			return CodecSpecification.ARRAY;
		}
		if (clazz.isEnum()) {
			return CodecSpecification.ENUMERATION;
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			return CodecSpecification.COLLECTION;
		}
		if (Map.class.isAssignableFrom(clazz)) {
			return CodecSpecification.MAP;
		}
		CodecSpecification specification = type2Specifitions.get(clazz);
		return specification != null ? specification : CodecSpecification.OBJECT;
	}

}
