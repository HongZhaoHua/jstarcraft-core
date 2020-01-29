package com.jstarcraft.core.codec.specification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.codec.exception.CodecDefinitionException;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.utility.PressUtility;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * 编解码定义(核心类)
 * 
 * @author Birdy
 */
public class CodecDefinition {

    private static final Comparator<Type> typeComparator = new Comparator<Type>() {
        @Override
        public int compare(Type left, Type right) {
            return left.getTypeName().compareTo(right.getTypeName());
        }
    };

    /** 代号管理器 */
    private AtomicInteger codeManager = new AtomicInteger();
    /** 索引-定义映射 */
    private ArrayList<ClassDefinition> code2Definitions;
    /** 类型-定义映射 */
    private HashMap<Type, ClassDefinition> type2Definitions = new HashMap<>();

    private CodecDefinition() {
    }

    /**
     * 获取类型定义
     * 
     * @param index
     * @return
     */
    public Collection<ClassDefinition> getClassDefinitions() {
        return Collections.unmodifiableCollection(code2Definitions);
    }

    /**
     * 获取类型定义
     * 
     * @param index
     * @return
     */
    public ClassDefinition getClassDefinition(int index) {
        return code2Definitions.get(index - 1);
    }

    /**
     * 获取类型定义
     * 
     * @param clazz
     * @return
     */
    public ClassDefinition getClassDefinition(Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        ClassDefinition definition = type2Definitions.get(clazz);
        if (definition != null) {
            // 存在类型映射
            return definition;
        }
        synchronized (this) {
            Class<?> type = clazz;
            while (type != Object.class) {
                type = type.getSuperclass();
                if (type == Object.class) {
                    throw new CodecDefinitionException(clazz.getName());
                }
                definition = type2Definitions.get(type);
                if (definition != null) {
                    type2Definitions.put(clazz, definition);
                }
            }
        }
        return definition;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        CodecDefinition that = (CodecDefinition) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.code2Definitions, that.code2Definitions);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(code2Definitions);
        return hash.toHashCode();
    }

    private static void findDependentClasses(Class<?> clazz, Collection<Class<?>> classes) {
        ReflectionUtility.doWithFields(clazz, (field) -> {
            if (!classes.contains(field.getGenericType())) {
                findDependentClasses(field.getGenericType(), classes);
            }
        }, (field) -> {
            return !(Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()));
        });
    }

    private static void findDependentClasses(Type type, Collection<Class<?>> classes) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            classes.add(clazz);
            findDependentClasses(clazz, classes);
        } else if (type instanceof GenericArrayType) {
            // 数组类型
            GenericArrayType genericArrayType = (GenericArrayType) type;
            findDependentClasses(genericArrayType.getGenericComponentType(), classes);
        } else if (type instanceof ParameterizedType) {
            // 泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            findDependentClasses(parameterizedType.getRawType(), classes);
            for (Type value : parameterizedType.getActualTypeArguments()) {
                findDependentClasses(value, classes);
            }
        } else if (type instanceof TypeVariable) {
            // 擦拭类型
            throw new CodecException(type + "为擦拭类型");
        } else if (type instanceof WildcardType) {
            // 通配类型
            throw new CodecException(type + "为通配类型");
        }
    }

    public static CodecDefinition instanceOf(Collection<? extends Type> types) {
        CodecDefinition definition = new CodecDefinition();
        // 遍历与排序所有依赖的类型
        TreeSet<Class<?>> classes = new TreeSet<>(typeComparator);
        classes.addAll(Specification.type2Specifitions.keySet());
        for (Type type : types) {
            findDependentClasses(type, classes);
        }
        Object2IntMap<Class<?>> codes = new Object2IntOpenHashMap<>();
        for (Class<?> clazz : classes) {
            if (clazz.isArray()) {
                continue;
            }
            int code = definition.codeManager.incrementAndGet();
            codes.put(clazz, code);
        }

        definition.code2Definitions = new ArrayList<>(classes.size());

        for (Class<?> clazz : classes) {
            if (clazz.isArray()) {
                continue;
            }
            if (definition.type2Definitions.get(clazz) == null) {
                ClassDefinition classDefinition = ClassDefinition.instanceOf(clazz, codes);
                definition.code2Definitions.add(classDefinition);
                definition.type2Definitions.put(clazz, classDefinition);
            }
        }
        return definition;
    }

    public static CodecDefinition instanceOf(Type... types) {
        return instanceOf(Arrays.asList(types));
    }

    public static CodecDefinition fromBytes(byte[] bytes) {
        try {
            CodecDefinition definition = new CodecDefinition();
            // 解压解密
            byte[] unzip = PressUtility.unzip(bytes, 30, TimeUnit.SECONDS);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(unzip);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            int size = dataInputStream.readInt();
            definition.code2Definitions = new ArrayList<>(size);
            while (dataInputStream.available() > 0) {
                ClassDefinition classDefinition = ClassDefinition.readFrom(dataInputStream);
                Type clazz = classDefinition.getType();
                definition.code2Definitions.add(classDefinition);
                definition.type2Definitions.put(clazz, classDefinition);
            }
            return definition;
        } catch (Exception exception) {
            throw new CodecDefinitionException(exception);
        }
    }

    public static byte[] toBytes(CodecDefinition definition) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            int size = definition.code2Definitions.size();
            dataOutputStream.writeInt(size);
            for (ClassDefinition classDefinition : definition.code2Definitions) {
                ClassDefinition.writeTo(classDefinition, dataOutputStream);
            }

            // 加密压缩
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byte[] zip = PressUtility.zip(bytes, 5);
            return zip;
        } catch (Exception exception) {
            throw new CodecDefinitionException(exception);
        }
    }

}
