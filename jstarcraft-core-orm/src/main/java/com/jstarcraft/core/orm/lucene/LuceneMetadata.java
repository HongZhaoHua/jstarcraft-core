package com.jstarcraft.core.orm.lucene;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.exception.OrmException;
import com.jstarcraft.core.orm.lucene.annotation.LuceneId;
import com.jstarcraft.core.orm.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.orm.lucene.annotation.LuceneSort;
import com.jstarcraft.core.orm.lucene.annotation.LuceneStore;
import com.jstarcraft.core.orm.lucene.converter.IdConverter;
import com.jstarcraft.core.orm.lucene.converter.IndexConverter;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.converter.SortConverter;
import com.jstarcraft.core.orm.lucene.converter.StoreConverter;
import com.jstarcraft.core.utility.KeyValue;

public class LuceneMetadata implements OrmMetadata {

    public static final String LUCENE_ID = "_id";

    public static final String LUCENE_VERSION = "_version";

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

    private LuceneContext context;

    /**
     * 构造方法
     * 
     * @param metadata
     */
    LuceneMetadata(Class<?> clazz) {
        ormClass = clazz;
        ormName = clazz.getName();
        ReflectionUtility.doWithFields(ormClass, (field) -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }
            Class<?> type = field.getType();
            fields.put(field.getName(), type);
            if (field.isAnnotationPresent(LuceneId.class)) {
                primaryName = field.getName();
                primaryClass = type;
            }
            if (field.isAnnotationPresent(LuceneIndex.class)) {
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
    @SuppressWarnings("unchecked")
    public <K extends Serializable> Class<K> getPrimaryClass() {
        return primaryClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentityObject> Class<T> getOrmClass() {
        return ormClass;
    }

    @Override
    public String getVersionName() {
        return LUCENE_VERSION;
    }

}
