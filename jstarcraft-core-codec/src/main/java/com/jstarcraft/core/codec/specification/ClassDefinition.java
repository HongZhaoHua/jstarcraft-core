package com.jstarcraft.core.codec.specification;

import java.beans.PropertyDescriptor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.jstarcraft.core.codec.annotation.ExcludeProperty;
import com.jstarcraft.core.codec.annotation.IncludeProperty;
import com.jstarcraft.core.codec.annotation.ProtocolConfiguration;
import com.jstarcraft.core.codec.annotation.ProtocolConfiguration.Mode;
import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.ReflectionUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.core.utility.TypeUtility;

/**
 * 类型定义
 * 
 * @author Birdy
 */
public class ClassDefinition implements Comparable<ClassDefinition> {

	/** 代号 */
	private int code;
	/** 类型 */
	private Class<?> clazz;
	/** 构造器 */
	private Constructor<?> constructor;
	/** 属性 */
	private PropertyDefinition[] properties;
	/** 名称 */
	private String name;
	/** 规范 */
	private CodecSpecification specification;

	private ClassDefinition(int code, Class<?> clazz, TreeSet<PropertyDefinition> properties, CodecSpecification specification) {
		this.code = code;
		this.clazz = clazz;
		this.name = clazz.getName();
		this.specification = specification;
		this.properties = properties.toArray(new PropertyDefinition[properties.size()]);
		// 不是所有类型都有无参数构造器
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (constructor.getParameterTypes().length == 0) {
				constructor.setAccessible(true);
				this.constructor = constructor;
				break;
			}
		}
		// if (this.constructor == null && !this.clazz.isEnum()) {
		// String message = StringUtility.format("类型[{}]缺乏无参数构造器,不符合编解码规范",
		// clazz.getName());
		// throw new CodecException(message);
		// }
	}

	public Object getInstance() throws Exception {
		if (clazz == null) {
			return new HashMap<String, Object>();
		}
		return constructor.newInstance();
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return clazz;
	}

	public CodecSpecification getSpecification() {
		return specification;
	}

	public PropertyDefinition[] getProperties() {
		return properties;
	}

	@Override
	public int compareTo(ClassDefinition that) {
		CompareToBuilder comparator = new CompareToBuilder();
		comparator.append(this.code, that.code);
		return comparator.toComparison();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		ClassDefinition that = (ClassDefinition) object;
		EqualsBuilder equal = new EqualsBuilder();
		equal.append(this.code, that.code);
		return equal.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hash = new HashCodeBuilder();
		hash.append(code);
		return hash.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder string = new ToStringBuilder(this);
		string.append(code);
		string.append(name);
		return string.toString();
	}

	public static ClassDefinition instanceOf(Class<?> clazz, Map<Class<?>, Integer> codes) {
		ProtocolConfiguration protocolConfiguration = clazz.getAnnotation(ProtocolConfiguration.class);
		TreeSet<PropertyDefinition> properties = new TreeSet<PropertyDefinition>();
		CodecSpecification specification = CodecSpecification.getSpecification(clazz);
		if (specification == CodecSpecification.OBJECT) {
			if (protocolConfiguration == null || protocolConfiguration.mode() == Mode.FIELD) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(ExcludeProperty.class)) {
						continue;
					}
					int modifiers = field.getModifiers();
					if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
						continue;
					}
					String name = field.getName();
					Type type = field.getGenericType();
					Integer code = codes.get(TypeUtility.getArrayComponentType(type));
					if (code == null) {
						code = codes.get(TypeUtility.getRawType(type, null));
					}
					PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, field);
					properties.add(property);
				}
			} else if (protocolConfiguration.mode() == Mode.METHOD) {
				PropertyDescriptor[] descriptors = ReflectionUtility.getPropertyDescriptors(clazz);
				for (PropertyDescriptor descriptor : descriptors) {
					String name = descriptor.getName();
					if (name.equals("class")) {
						continue;
					}
					Method getter = descriptor.getReadMethod();
					if (getter == null) {
						continue;
					}
					if (getter.isAnnotationPresent(ExcludeProperty.class)) {
						continue;
					}
					Method setter = descriptor.getWriteMethod();
					Type type = descriptor.getPropertyType();
					if (getter.getGenericReturnType() instanceof ParameterizedType) {
						type = getter.getGenericReturnType();
					}
					Integer code = codes.get(TypeUtility.getArrayComponentType(type));
					if (code == null) {
						code = codes.get(TypeUtility.getRawType(type, null));
					}
					PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, getter, setter);
					properties.add(property);
				}
			} else {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(IncludeProperty.class)) {
						continue;
					}
					int modifiers = field.getModifiers();
					if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
						continue;
					}
					String name = field.getName();
					Type type = field.getGenericType();
					int code = codes.get(TypeUtility.getRawType(type, null));
					PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, field);
					properties.add(property);
				}
				PropertyDescriptor[] descriptors = ReflectionUtility.getPropertyDescriptors(clazz);
				for (PropertyDescriptor descriptor : descriptors) {
					String name = descriptor.getName();
					if (name.equals("class")) {
						continue;
					}
					Method getter = descriptor.getReadMethod();
					if (getter == null) {
						continue;
					}
					if (getter.isAnnotationPresent(ExcludeProperty.class)) {
						continue;
					}
					Method setter = descriptor.getWriteMethod();
					Type type = descriptor.getPropertyType();
					if (getter.getGenericReturnType() instanceof ParameterizedType) {
						type = getter.getGenericReturnType();
					}
					Integer code = codes.get(TypeUtility.getArrayComponentType(type));
					if (code == null) {
						code = codes.get(TypeUtility.getRawType(type, null));
					}
					PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, getter, setter);
					properties.add(property);
				}
			}
		}
		return new ClassDefinition(codes.get(clazz), clazz, properties, specification);
	}

	public static ClassDefinition readFrom(DataInputStream in) throws IOException {
		short code = in.readShort();
		byte specification = in.readByte();
		int length = in.readShort();
		byte[] bytes = new byte[length];
		in.read(bytes);
		String className = new String(bytes, StringUtility.CHARSET);
		Class<?> clazz;
		try {
			clazz = ClassUtility.getClass(className, false);
		} catch (ClassNotFoundException exception) {
			throw new IOException(exception);
		}
		length = in.readShort();
		TreeSet<PropertyDefinition> properties = new TreeSet<>();
		for (int index = 0, size = length; index < size; index++) {
			// 属性名
			short propertyCode = in.readShort();
			length = in.readShort();
			bytes = new byte[length];
			in.read(bytes);
			String name = new String(bytes, StringUtility.CHARSET);
			Type type = null;
			Field field = ReflectionUtility.findField(clazz, name);
			if (field != null) {
				type = field.getGenericType();
				PropertyDefinition property = PropertyDefinition.instanceOf(name, propertyCode, type, field);
				properties.add(property);
			} else {
				PropertyDescriptor[] descriptors = ReflectionUtility.getPropertyDescriptors(clazz);
				for (PropertyDescriptor descriptor : descriptors) {
					if (name.equals(descriptor.getName())) {
						Method getter = descriptor.getReadMethod();
						Method setter = descriptor.getWriteMethod();
						type = descriptor.getPropertyType();
						PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, getter, setter);
						properties.add(property);
						break;
					}
				}
			}
			if (type == null) {
				throw new IOException();
			}
		}
		return new ClassDefinition(code, clazz, properties, CodecSpecification.getSpecification(clazz));
	}

	public static void writeTo(ClassDefinition definition, DataOutputStream out) throws IOException {
		int code = definition.getCode();
		CodecSpecification specification = definition.getSpecification();
		String name = definition.getName();
		byte[] bytes = name.getBytes(StringUtility.CHARSET);
		out.writeShort((short) code);
		out.writeByte(specification.getCode());
		out.writeShort((short) bytes.length);
		out.write(bytes);
		out.writeShort((short) definition.properties.length);
		for (PropertyDefinition property : definition.properties) {
			bytes = property.getName().getBytes(StringUtility.CHARSET);
			out.writeShort((short) property.getCode());
			out.writeShort((short) bytes.length);
			out.write(bytes);
		}
	}

}
