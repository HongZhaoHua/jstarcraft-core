package com.jstarcraft.core.codec.specification;

import java.beans.PropertyDescriptor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import com.jstarcraft.core.codec.exception.CodecDefinitionException;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.StringUtility;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

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
    private Specification specification;

    private static final byte[] codes = new byte[Specification.values().length];

    static {
        /** 数组 */
        codes[Specification.ARRAY.ordinal()] = (byte) 0x00;
        /** 布尔 */
        codes[Specification.BOOLEAN.ordinal()] = (byte) 0x10;
        /** 集合 */
        codes[Specification.COLLECTION.ordinal()] = (byte) 0x20;
        /** 枚举 */
        codes[Specification.ENUMERATION.ordinal()] = (byte) 0x30;
        /** 时间 */
        codes[Specification.INSTANT.ordinal()] = (byte) 0x40;
        /** 映射 */
        codes[Specification.MAP.ordinal()] = (byte) 0x50;
        /** 数值 */
        codes[Specification.NUMBER.ordinal()] = (byte) 0x60;
        /** 对象 */
        codes[Specification.OBJECT.ordinal()] = (byte) 0x70;
        /** 字符串 */
        codes[Specification.STRING.ordinal()] = (byte) 0x80;
        /** 类型 */
        codes[Specification.TYPE.ordinal()] = (byte) 0x90;
        /** 未知 */
        codes[Specification.VOID.ordinal()] = (byte) 0xA0;
    }

    /**
     * 通过指定规范获取代号
     * 
     * @param specification
     * @return
     */
    public static byte getCode(Specification specification) {
        return codes[specification.ordinal()];
    }

    private ClassDefinition(int code, Class<?> clazz, TreeSet<PropertyDefinition> properties, Specification specification) {
        this.code = code;
        this.clazz = clazz;
        this.name = clazz.getName();
        this.specification = specification;
        this.properties = properties.toArray(new PropertyDefinition[properties.size()]);
        // 不是所有类型都有无参数构造器
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                ReflectionUtility.makeAccessible(constructor);
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

    public Specification getSpecification() {
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

    public static ClassDefinition instanceOf(Class<?> clazz, Object2IntMap<Class<?>> codes) {
        ProtocolConfiguration protocolConfiguration = clazz.getAnnotation(ProtocolConfiguration.class);
        TreeSet<PropertyDefinition> properties = new TreeSet<PropertyDefinition>();
        Specification specification = Specification.getSpecification(clazz);
        if (specification == Specification.OBJECT) {
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
                    int code = codes.getInt(TypeUtility.getArrayComponentType(type));
                    if (code == 0) {
                        code = codes.getInt(TypeUtility.getRawType(type, null));
                    }
                    PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, field);
                    properties.add(property);
                }
            } else if (protocolConfiguration.mode() == Mode.METHOD) {
                Map<String, PropertyDescriptor> descriptors = ReflectionUtility.getPropertyDescriptors(clazz);
                for (PropertyDescriptor descriptor : descriptors.values()) {
                    String name = descriptor.getName();
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
                    int code = codes.getInt(TypeUtility.getArrayComponentType(type));
                    if (code == 0) {
                        code = codes.getInt(TypeUtility.getRawType(type, null));
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
                    int code = codes.getInt(TypeUtility.getRawType(type, null));
                    PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, field);
                    properties.add(property);
                }
                Map<String, PropertyDescriptor> descriptors = ReflectionUtility.getPropertyDescriptors(clazz);
                for (PropertyDescriptor descriptor : descriptors.values()) {
                    String name = descriptor.getName();
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
                    int code = codes.getInt(TypeUtility.getArrayComponentType(type));
                    if (code == 0) {
                        code = codes.getInt(TypeUtility.getRawType(type, null));
                    }
                    PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, getter, setter);
                    properties.add(property);
                }
            }
        }
        return new ClassDefinition(codes.getInt(clazz), clazz, properties, specification);
    }

    public static ClassDefinition readFrom(DataInputStream in) {
        try {
            short code = in.readShort();
            byte specification = in.readByte();
            int length = in.readShort();
            byte[] bytes = new byte[length];
            in.read(bytes);
            String className = new String(bytes, StringUtility.CHARSET);
            Class<?> clazz = ClassUtility.getClass(className, false);
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
                    Map<String, PropertyDescriptor> descriptors = ReflectionUtility.getPropertyDescriptors(clazz);
                    PropertyDescriptor descriptor = descriptors.get(name);
                    if (descriptor != null) {
                        Method getter = descriptor.getReadMethod();
                        Method setter = descriptor.getWriteMethod();
                        type = descriptor.getPropertyType();
                        PropertyDefinition property = PropertyDefinition.instanceOf(name, code, type, getter, setter);
                        properties.add(property);
                    }
                }
                if (type == null) {
                    throw new NullPointerException();
                }
            }
            return new ClassDefinition(code, clazz, properties, Specification.getSpecification(clazz));
        } catch (Exception exception) {
            throw new CodecDefinitionException(exception);
        }
    }

    public static void writeTo(ClassDefinition definition, DataOutputStream out) {
        try {
            int code = definition.getCode();
            Specification specification = definition.getSpecification();
            String name = definition.getName();
            byte[] bytes = name.getBytes(StringUtility.CHARSET);
            out.writeShort((short) code);
            out.writeByte(getCode(specification));
            out.writeShort((short) bytes.length);
            out.write(bytes);
            out.writeShort((short) definition.properties.length);
            for (PropertyDefinition property : definition.properties) {
                bytes = property.getName().getBytes(StringUtility.CHARSET);
                out.writeShort((short) property.getCode());
                out.writeShort((short) bytes.length);
                out.write(bytes);
            }
        } catch (Exception exception) {
            throw new CodecDefinitionException(exception);
        }
    }

}
