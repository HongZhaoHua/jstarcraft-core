package com.jstarcraft.core.storage.elasticsearch;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Elasticsearch元信息
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class ElasticsearchMetadata implements StorageMetadata {

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
    ElasticsearchMetadata(Class<?> clazz) {
        Document document = clazz.getAnnotation(Document.class);
        if (document == null) {
            throw new IllegalArgumentException();
        }
        ormName = document.indexName();
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
            if (field.isAnnotationPresent(Field.class)) {
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

}
