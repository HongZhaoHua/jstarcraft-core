package com.jstarcraft.core.storage.mongo;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Mongo元信息
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class MongoMetadata implements StorageMetadata {

    public static final String mongoId = "_id";

    private static final HashSet<Class<?>> mongoTypes = new HashSet<>();
    static {
        // 布尔规范
        mongoTypes.add(AtomicBoolean.class);
        mongoTypes.add(boolean.class);
        mongoTypes.add(Boolean.class);

        // 数值规范
        mongoTypes.add(AtomicInteger.class);
        mongoTypes.add(AtomicLong.class);
        mongoTypes.add(byte.class);
        mongoTypes.add(short.class);
        mongoTypes.add(int.class);
        mongoTypes.add(long.class);
        mongoTypes.add(float.class);
        mongoTypes.add(double.class);
        mongoTypes.add(Byte.class);
        mongoTypes.add(Short.class);
        mongoTypes.add(Integer.class);
        mongoTypes.add(Long.class);
        mongoTypes.add(Float.class);
        mongoTypes.add(Double.class);
        mongoTypes.add(BigInteger.class);
        mongoTypes.add(BigDecimal.class);

        // 字符规范
        mongoTypes.add(char.class);
        mongoTypes.add(Character.class);
        mongoTypes.add(String.class);

        // 时间规范
        mongoTypes.add(Date.class);
        mongoTypes.add(Instant.class);
    }

    /** 实体名称 */
    private String ormName;
    /** 实体类型 */
    private Class ormClass;
    /** 主键名称 */
    private String primaryName;
    /** 主键类型 */
    private Class primaryClass;
    /** 字段映射(名称-类型) */
    private Map<String, Class<?>> fields = new HashMap<>();
    /** 索引域名 */
    private Collection<String> indexNames = new HashSet<>();
    /** 版本号域名 */
    private String versionName;

    /**
     * 构造方法
     * 
     * @param metadata
     */
    MongoMetadata(Class<?> clazz) {
        Document document = clazz.getAnnotation(Document.class);
        if (document == null) {
            throw new IllegalArgumentException();
        }
        ormName = document.collection();
        if (StringUtility.isBlank(ormName)) {
            ormName = clazz.getSimpleName();
            ormName = ormName.substring(0, 1).toLowerCase() + ormName.substring(1, ormName.length());
        }
        ormClass = clazz;
        ReflectionUtility.doWithFields(ormClass, (field) -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }
            if (field.isAnnotationPresent(Version.class)) {
                versionName = field.getName();
                return;
            }
            Class<?> type = field.getType();
            fields.put(field.getName(), type);
            if (field.isAnnotationPresent(Id.class)) {
                primaryName = field.getName();
                primaryClass = type;
            }
            if (field.isAnnotationPresent(Indexed.class)) {
                indexNames.add(field.getName());
            }
        });
    }

    @Override
    public String getOrmName() {
        return ormName;
    }

    @Override
    public Map<String, Class<?>> getFields() {
        return fields;
    }

    @Override
    public String getPrimaryName() {
        return primaryName;
    }

    @Override
    public Collection<String> getIndexNames() {
        return indexNames;
    }

    @Override
    public String getVersionName() {
        return versionName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K extends Serializable> Class<K> getPrimaryClass() {
        return primaryClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentityObject> Class<T> getOrmClass() {
        return ormClass;
    }

    boolean isJsonField(String name) {
        return mongoTypes.contains(fields.get(name));
    }

}
