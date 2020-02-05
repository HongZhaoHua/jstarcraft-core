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
    public LuceneMetadata(Class<?> clazz, LuceneContext context) {
        this.ormClass = clazz;
        this.ormName = clazz.getName();
        ReflectionUtility.doWithFields(this.ormClass, (field) -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }
            Class<?> type = field.getType();
            this.fields.put(field.getName(), type);
            if (field.isAnnotationPresent(LuceneId.class)) {
                this.primaryName = field.getName();
                this.primaryClass = type;
            }
            if (field.isAnnotationPresent(LuceneIndex.class)) {
                this.indexNames.add(field.getName());
            }
        });
        this.context = context;
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
    public Object decode(Document document) {
        try {
            NavigableMap<String, IndexableField> indexables = new TreeMap<>();
            for (IndexableField indexable : document) {
                indexables.put(indexable.name(), indexable);
            }
            Object instance = this.context.getInstance(ormClass);
            {
                KeyValue<Field, IdConverter> keyValue = this.context.getIdKeyValue(ormClass);
                if (keyValue != null) {
                    Field field = keyValue.getKey();
                    IdConverter converter = keyValue.getValue();
                    LuceneId annotation = field.getAnnotation(LuceneId.class);
                    Type type = field.getGenericType();
                    IndexableField indexable = indexables.get(LuceneMetadata.LUCENE_ID);
                    Object data = converter.decode(type, indexable.stringValue());
                    field.set(instance, data);
                }
            }
            for (KeyValue<Field, StoreConverter> keyValue : this.context.getStoreKeyValues(ormClass)) {
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
            throw new OrmException(exception);
        }
    }

    /**
     * 编码
     * 
     * @param object
     * @return
     */
    public Document encode(Object object) {
        try {
            Document document = new Document();
            {
                KeyValue<Field, IdConverter> keyValue = this.context.getIdKeyValue(ormClass);
                if (keyValue != null) {
                    Field field = keyValue.getKey();
                    IdConverter converter = keyValue.getValue();
                    LuceneId annotation = field.getAnnotation(LuceneId.class);
                    Type type = field.getGenericType();
                    Object data = field.get(object);
                    String id = converter.encode(type, data);
                    IndexableField indexable = null;
                    indexable = new StringField(LuceneMetadata.LUCENE_ID, id, Store.YES);
                    document.add(indexable);
                    indexable = new BinaryDocValuesField(LuceneMetadata.LUCENE_ID, new BytesRef(id));
                    document.add(indexable);
                    long version = System.currentTimeMillis();
                    indexable = new NumericDocValuesField(LuceneMetadata.LUCENE_VERSION, version);
                    document.add(indexable);
                }
            }
            for (KeyValue<Field, IndexConverter> keyValue : this.context.getIndexKeyValues(ormClass)) {
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
            for (KeyValue<Field, SortConverter> keyValue : this.context.getSortKeyValues(ormClass)) {
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
            for (KeyValue<Field, StoreConverter> keyValue : this.context.getStoreKeyValues(ormClass)) {
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
            throw new OrmException(exception);
        }
    }

}
