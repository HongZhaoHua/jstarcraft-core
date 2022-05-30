package com.jstarcraft.core.storage.berkeley;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyOperationException;
import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Berkeley元信息
 * 
 * @author Birdy
 */
public class BerkeleyMetadata implements StorageMetadata, Comparable<BerkeleyMetadata> {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyMetadata.class);

    /** 缓存 */
    private static ConcurrentHashMap<Class<?>, BerkeleyMetadata> caches = new ConcurrentHashMap<>();

    /** 实际类型 */
    private final Class ormClass;
    /** 主键类型 */
    private final Class primaryClass;
    /** 主键字段 */
    private final Field primaryField;
    /** 次键字段 */
    private final HashMap<String, Field> secondaryFields;

    /** 版本字段 */
    private final Field versionField;
    /** 存储空间 */
    private final String storeName;

    /** 依赖类型集合 */
    private final HashSet<Class<?>> relativeClasses;
    /** 存储类型(带{@link Entity}注解的类型) */
    private final Class storeClass;

    /**
     * 构造方法
     * 
     * @param clazz
     */
    private BerkeleyMetadata(Class<?> clazz) {
        ormClass = clazz;

        List<Field> fields = ReflectionUtility.findFields(clazz, PrimaryKey.class);
        if (fields.size() != 1) {
            throw new IllegalArgumentException();
        }
        primaryField = fields.get(0);
        ReflectionUtility.makeAccessible(primaryField);
        primaryClass = ClassUtility.primitiveToWrapper(primaryField.getType());

        relativeClasses = new HashSet<>();
        secondaryFields = new HashMap<>();
        fields = ReflectionUtility.findFields(clazz, SecondaryKey.class);
        for (Field field : fields) {
            secondaryFields.put(field.getName(), field);
            SecondaryKey annotation = field.getAnnotation(SecondaryKey.class);
            Class<?> relativeClass = annotation.relatedEntity();
            if (relativeClass != void.class && relativeClass != ormClass) {
                relativeClasses.add(relativeClass);
            }
        }

        storeClass = AnnotationUtils.findAnnotationDeclaringClass(Entity.class, clazz);
        if (storeClass == null) {
            throw new IllegalArgumentException();
        }
        BerkeleyConfiguration configuration = clazz.getAnnotation(BerkeleyConfiguration.class);
        if (configuration == null) {
            throw new IllegalArgumentException();
        }
        storeName = configuration.store();

        if (StringUtility.isNotBlank(configuration.version())) {
            versionField = ReflectionUtility.findField(clazz, configuration.version());
            ReflectionUtility.makeAccessible(versionField);
        } else {
            versionField = null;
        }
    }

    @Override
    public String getOrmName() {
        return ormClass.getSimpleName();
    }

    @Override
    public String getPrimaryName() {
        return primaryField.getName();
    }

    @Override
    public <K extends Serializable> Class<K> getPrimaryClass() {
        return primaryClass;
    }

    @Override
    public <T extends IdentityObject> Class<T> getOrmClass() {
        return ormClass;
    }

    @Override
    public Map<String, Class<?>> getFields() {
        // TODO 考虑取消
        return null;
    }

    @Override
    public Collection<String> getIndexNames() {
        return secondaryFields.keySet();
    }

    @Override
    public String getVersionName() {
        if (versionField == null) {
            return null;
        }
        return versionField.getName();
    }

    /**
     * 获取指定实例的主键值
     * 
     * @param instance
     * @return
     */
    public Object getPrimaryValue(Object instance) {
        try {
            return primaryField.get(instance);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("获取主键值时异常", exception);
        }
    }

    /**
     * 获取指定实例的版本值
     * 
     * @param instance
     * @return
     */
    public int getVersionValue(Object instance) {
        if (versionField == null) {
            throw new BerkeleyOperationException("版本字段不存在");
        }
        try {
            return versionField.getInt(instance);
        } catch (IllegalAccessException exception) {
            throw new BerkeleyOperationException("获取版本值时异常", exception);
        }
    }

    /**
     * 设置指定实例的版本值
     * 
     * @param instance
     * @param value
     */
    public void setVersionValue(Object instance, int value) {
        if (versionField == null) {
            throw new BerkeleyOperationException("版本字段不存在");
        }
        try {
            versionField.setInt(instance, value);
        } catch (IllegalAccessException exception) {
            throw new BerkeleyOperationException("设置版本值时异常", exception);
        }
    }

    /**
     * 获取指定名称的次键字段
     * 
     * @param name
     * @return
     */
    public Field getSecondaryField(String name) {
        return secondaryFields.get(name);
    }

    public Class<?> getStoreClass() {
        return storeClass;
    }

    public String getStoreName() {
        return storeName;
    }

    @Override
    public int compareTo(BerkeleyMetadata that) {
        if (this.equals(that)) {
            return 0;
        }
        if (that.relativeClasses.contains(this.ormClass)) {
            return -1;
        }
        if (this.relativeClasses.contains(that.ormClass)) {
            return 1;
        }
        if (that.storeClass == this.ormClass) {
            return -1;
        }
        if (this.storeClass == that.ormClass) {
            return 1;
        }
        return this.ormClass.getName().compareTo(that.ormClass.getName());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        BerkeleyMetadata that = (BerkeleyMetadata) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.ormClass, that.ormClass);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(ormClass);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        return "BerkeleyMetadata [ormClass=" + ormClass.getName() + "]";
    }

    public static BerkeleyMetadata instanceOf(Class<?> clazz) {
        synchronized (clazz) {
            BerkeleyMetadata metadata = caches.get(clazz);
            if (metadata == null) {
                metadata = new BerkeleyMetadata(clazz);
                caches.put(clazz, metadata);
            }
            return metadata;
        }
    }

}
