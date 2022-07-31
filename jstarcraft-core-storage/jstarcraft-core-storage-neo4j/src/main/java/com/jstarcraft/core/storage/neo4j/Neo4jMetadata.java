package com.jstarcraft.core.storage.neo4j;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.RelationshipEntity;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Neo4j元信息
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class Neo4jMetadata implements StorageMetadata {

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
    Neo4jMetadata(Class<?> clazz) {
        NodeEntity node = clazz.getAnnotation(NodeEntity.class);
        RelationshipEntity relation = clazz.getAnnotation(RelationshipEntity.class);
        if (node != null && relation != null) {
            throw new IllegalArgumentException();
        }
        if (node != null) {
            ormName = node.label();
        } else if (relation != null) {
            ormName = relation.type();
        } else {
            throw new IllegalArgumentException();
        }
        if (StringUtility.isBlank(ormName)) {
            ormName = clazz.getSimpleName();
        }
        ormClass = clazz;
        ReflectionUtility.doWithFields(ormClass, (field) -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }
            Class<?> type = field.getType();
            fields.put(field.getName(), type);
            if (field.isAnnotationPresent(Id.class)) {
                primaryName = field.getName();
                primaryClass = type;
            }
            if (field.isAnnotationPresent(Index.class)) {
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
