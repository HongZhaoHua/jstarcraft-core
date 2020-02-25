package com.jstarcraft.core.storage.lucene;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneConfiguration;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.annotation.LuceneSort;
import com.jstarcraft.core.storage.lucene.annotation.LuceneStore;
import com.jstarcraft.core.storage.lucene.converter.IndexConverter;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.storage.lucene.converter.SortConverter;
import com.jstarcraft.core.storage.lucene.converter.StoreConverter;
import com.jstarcraft.core.utility.KeyValue;

public class LuceneMetadata implements StorageMetadata {

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

    private Map<String, KeyValue<Field, IndexConverter>> indexKeyValues;

    private Map<String, KeyValue<Field, SortConverter>> sortKeyValues;

    private Map<String, KeyValue<Field, StoreConverter>> storeKeyValues;

    /**
     * 构造方法
     * 
     * @param metadata
     */
    public LuceneMetadata(Class<?> clazz, LuceneContext context) {
        this.ormClass = clazz;
        this.ormName = clazz.getName();
        LuceneConfiguration configuration = clazz.getAnnotation(LuceneConfiguration.class);
        if (configuration != null) {
            Field field = ReflectionUtility.getField(this.ormClass, configuration.id());
            primaryName = field.getName();
            primaryClass = field.getType();
        } else {
            throw new IllegalArgumentException();
        }
        ReflectionUtility.doWithFields(this.ormClass, (field) -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }
            Class<?> type = field.getType();
            this.fields.put(field.getName(), type);
            if (field.isAnnotationPresent(LuceneIndex.class)) {
                this.indexNames.add(field.getName());
            }
        });
        this.context = context;
        this.indexKeyValues = new HashMap<>();
        for (Entry<Field, IndexConverter> term : this.context.getIndexKeyValues(this.ormClass).entrySet()) {
            KeyValue<Field, IndexConverter> keyVaule = new KeyValue<>(term);
            this.indexKeyValues.put(term.getKey().getName(), keyVaule);
        }
        this.sortKeyValues = new HashMap<>();
        for (Entry<Field, SortConverter> term : this.context.getSortKeyValues(this.ormClass).entrySet()) {
            KeyValue<Field, SortConverter> keyVaule = new KeyValue<>(term);
            this.sortKeyValues.put(term.getKey().getName(), keyVaule);
        }
        this.storeKeyValues = new HashMap<>();
        for (Entry<Field, StoreConverter> term : this.context.getStoreKeyValues(this.ormClass).entrySet()) {
            KeyValue<Field, StoreConverter> keyVaule = new KeyValue<>(term);
            this.storeKeyValues.put(term.getKey().getName(), keyVaule);
        }
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

    /**
     * 解码
     * 
     * @param document
     * @return
     */
    public Object decodeDocument(Document document) {
        try {
            NavigableMap<String, IndexableField> indexables = new TreeMap<>();
            for (IndexableField indexable : document) {
                indexables.put(indexable.name(), indexable);
            }
            Object instance = this.context.getInstance(ormClass);
            for (Entry<Field, StoreConverter> keyValue : this.context.getStoreKeyValues(ormClass).entrySet()) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                StoreConverter converter = keyValue.getValue();
                LuceneStore annotation = field.getAnnotation(LuceneStore.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = converter.decode(this.context, path, field, annotation, type, indexables);
                field.set(instance, data);
            }
            return instance;
        } catch (Exception exception) {
            // TODO
            throw new StorageException(exception);
        }
    }

    /**
     * 编码
     * 
     * @param object
     * @return
     */
    public Document encodeDocument(Object object) {
        try {
            Document document = new Document();
            for (Entry<Field, IndexConverter> keyValue : this.context.getIndexKeyValues(ormClass).entrySet()) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                IndexConverter converter = keyValue.getValue();
                LuceneIndex annotation = field.getAnnotation(LuceneIndex.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = field.get(object);
                for (IndexableField indexable : converter.convert(this.context, path, field, annotation, type, data)) {
                    document.add(indexable);
                }
            }
            for (Entry<Field, SortConverter> keyValue : this.context.getSortKeyValues(ormClass).entrySet()) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                SortConverter converter = keyValue.getValue();
                LuceneSort annotation = field.getAnnotation(LuceneSort.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = field.get(object);
                for (IndexableField indexable : converter.convert(this.context, path, field, annotation, type, data)) {
                    document.add(indexable);
                }
            }
            for (Entry<Field, StoreConverter> keyValue : this.context.getStoreKeyValues(ormClass).entrySet()) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                StoreConverter converter = keyValue.getValue();
                LuceneStore annotation = field.getAnnotation(LuceneStore.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = field.get(object);
                for (IndexableField indexable : converter.encode(this.context, path, field, annotation, type, data).values()) {
                    document.add(indexable);
                }
            }
            return document;
        } catch (Exception exception) {
            // TODO
            throw new StorageException(exception);
        }
    }

    public KeyValue<Field, IndexConverter> getIndexKeyValue(String field) {
        return indexKeyValues.get(field);
    }

    public KeyValue<Field, SortConverter> getSortKeyValue(String field) {
        return sortKeyValues.get(field);
    }

    public KeyValue<Field, StoreConverter> getStoreKeyValue(String field) {
        return storeKeyValues.get(field);
    }

}
