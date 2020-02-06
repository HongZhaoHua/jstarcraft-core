package com.jstarcraft.core.orm.lucene;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmIterator;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.OrmPagination;
import com.jstarcraft.core.orm.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.orm.lucene.annotation.LuceneSort;
import com.jstarcraft.core.orm.lucene.annotation.LuceneStore;
import com.jstarcraft.core.orm.lucene.converter.IdConverter;
import com.jstarcraft.core.orm.lucene.converter.IndexConverter;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.converter.SortConverter;
import com.jstarcraft.core.orm.lucene.converter.StoreConverter;
import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.floats.FloatList;

/**
 * Lucene访问器
 * 
 * @author Birdy
 *
 */
public class LuceneAccessor implements OrmAccessor {

    /** 元数据集合 */
    private HashMap<Class<?>, LuceneMetadata> metadatas = new HashMap<>();

    /** 标识转换器 */
    private IdConverter converter;

    /** 编解码器映射 */
    private LuceneContext context;

    private LuceneEngine engine;

    public LuceneAccessor(Collection<Class<?>> classes, IdConverter converter, LuceneEngine engine) {
        this.converter = converter;
        // 使用CodecDefinition分析依赖关系.
        CodecDefinition definition = CodecDefinition.instanceOf(classes);
        this.context = new LuceneContext(definition);
        this.engine = engine;
        for (Class<?> ormClass : classes) {
            LuceneMetadata metadata = new LuceneMetadata(ormClass, this.context);
            this.metadatas.put(ormClass, metadata);
        }
    }

    @Override
    public Collection<? extends OrmMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T get(Class<T> clazz, K id) {
        LuceneMetadata metadata = metadatas.get(clazz);
        KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(metadata.getPrimaryName());
        Field key = keyValue.getKey();
        IndexConverter value = keyValue.getValue();
        Query query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), OrmCondition.Equal, id);
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, 0, 1);
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean create(Class<T> clazz, T object) {
        LuceneMetadata metadata = metadatas.get(clazz);
        try {
            K id = object.getId();
            String key = converter.convert(id.getClass(), id);
            Document value = metadata.encodeDocument(object);
            engine.createDocument(key, value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean delete(Class<T> clazz, K id) {
        LuceneMetadata metadata = metadatas.get(clazz);
        try {
            String key = converter.convert(id.getClass(), id);
            engine.deleteDocument(key);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean delete(Class<T> clazz, T object) {
        LuceneMetadata metadata = metadatas.get(clazz);
        try {
            K id = object.getId();
            String key = converter.convert(id.getClass(), id);
            engine.deleteDocument(key);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean update(Class<T> clazz, T object) {
        LuceneMetadata metadata = metadatas.get(clazz);
        try {
            K id = object.getId();
            String key = converter.convert(id.getClass(), id);
            Document value = metadata.encodeDocument(object);
            engine.updateDocument(key, value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query;
        {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(metadata.getPrimaryName());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), OrmCondition.Between, from, to);
        }
        Sort sort;
        {
            KeyValue<Field, SortConverter> keyValue = metadata.getSortKeyValue(metadata.getPrimaryName());
            Field key = keyValue.getKey();
            SortConverter value = keyValue.getValue();
            sort = value.sort(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneSort.class), key.getGenericType(), false);
        }
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, sort, 0, 1);
        List<Document> documents = retrieve.getKey();
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        for (IndexableField indexable : documents.get(0).getFields(metadata.getPrimaryName())) {
            indexables.put(indexable.name(), indexable);
        }
        KeyValue<Field, StoreConverter> keyValue = metadata.getStoreKeyValue(metadata.getPrimaryName());
        Field key = keyValue.getKey();
        StoreConverter value = keyValue.getValue();
        return (K) value.decode(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneStore.class), key.getGenericType(), indexables);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query;
        {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(metadata.getPrimaryName());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), OrmCondition.Between, from, to);
        }
        Sort sort;
        {
            KeyValue<Field, SortConverter> keyValue = metadata.getSortKeyValue(metadata.getPrimaryName());
            Field key = keyValue.getKey();
            SortConverter value = keyValue.getValue();
            sort = value.sort(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneSort.class), key.getGenericType(), true);
        }
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, sort, 0, 1);
        List<Document> documents = retrieve.getKey();
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        for (IndexableField indexable : documents.get(0).getFields(metadata.getPrimaryName())) {
            indexables.put(indexable.name(), indexable);
        }
        KeyValue<Field, StoreConverter> keyValue = metadata.getStoreKeyValue(metadata.getPrimaryName());
        Field key = keyValue.getKey();
        StoreConverter value = keyValue.getValue();
        return (K) value.decode(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneStore.class), key.getGenericType(), indexables);
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, OrmCondition condition, String name, I... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, OrmCondition condition, String name, I... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> query(Class<T> clazz, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long count(Class<T> clazz) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(OrmIterator<T> iterator, Class<T> clazz, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(OrmIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(OrmIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

}
