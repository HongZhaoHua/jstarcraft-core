package com.jstarcraft.core.storage.lucene;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.berkeley.schema.BerkeleyAccessorFactory;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.annotation.LuceneSort;
import com.jstarcraft.core.storage.lucene.annotation.LuceneStore;
import com.jstarcraft.core.storage.lucene.converter.IdConverter;
import com.jstarcraft.core.storage.lucene.converter.IndexConverter;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.storage.lucene.converter.SortConverter;
import com.jstarcraft.core.storage.lucene.converter.StoreConverter;
import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.floats.FloatList;

/**
 * Lucene访问器
 * 
 * @author Birdy
 *
 */
public class LuceneAccessor implements StorageAccessor {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyAccessorFactory.class);

    private static final int BATCH_SIZE = 1000;

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
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        LuceneMetadata metadata = metadatas.get(clazz);
        KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(metadata.getPrimaryName());
        Field key = keyValue.getKey();
        IndexConverter value = keyValue.getValue();
        Query query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, id);
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, 0, 100);
        List<Document> documents = retrieve.getKey();
        if (documents.size() > 0) {
            return (T) metadata.decodeDocument(documents.get(0));
        } else {
            return null;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        LuceneMetadata metadata = metadatas.get(clazz);
        K id = object.getId();
        String key = converter.convert(id.getClass(), id);
        Document value = metadata.encodeDocument(object);
        engine.createDocument(key, value);
        return true;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        LuceneMetadata metadata = metadatas.get(clazz);
        String key = converter.convert(id.getClass(), id);
        engine.deleteDocument(key);
        return true;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        LuceneMetadata metadata = metadatas.get(clazz);
        K id = object.getId();
        String key = converter.convert(id.getClass(), id);
        engine.deleteDocument(key);
        return true;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
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
            query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Between, from, to);
        }
        Sort sort;
        {
            KeyValue<Field, SortConverter> keyValue = metadata.getSortKeyValue(metadata.getPrimaryName());
            Field key = keyValue.getKey();
            SortConverter value = keyValue.getValue();
            sort = value.sort(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneSort.class), key.getGenericType(), false);
        }
        KeyValue<Field, StoreConverter> keyValue = metadata.getStoreKeyValue(metadata.getPrimaryName());
        Field key = keyValue.getKey();
        StoreConverter value = keyValue.getValue();
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, sort, 0, 1);
        List<Document> documents = retrieve.getKey();
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        for (IndexableField indexable : documents.get(0).getFields(metadata.getPrimaryName())) {
            indexables.put(indexable.name(), indexable);
        }
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
            query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Between, from, to);
        }
        Sort sort;
        {
            KeyValue<Field, SortConverter> keyValue = metadata.getSortKeyValue(metadata.getPrimaryName());
            Field key = keyValue.getKey();
            SortConverter value = keyValue.getValue();
            sort = value.sort(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneSort.class), key.getGenericType(), true);
        }
        KeyValue<Field, StoreConverter> keyValue = metadata.getStoreKeyValue(metadata.getPrimaryName());
        Field key = keyValue.getKey();
        StoreConverter value = keyValue.getValue();
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, sort, 0, 1);
        List<Document> documents = retrieve.getKey();
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        for (IndexableField indexable : documents.get(0).getFields(metadata.getPrimaryName())) {
            indexables.put(indexable.name(), indexable);
        }
        return (K) value.decode(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneStore.class), key.getGenericType(), indexables);
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query;
        {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(name);
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), condition, values);
        }
        KeyValue<Field, StoreConverter> idKeyValue = metadata.getStoreKeyValue(metadata.getPrimaryName());
        Field idField = idKeyValue.getKey();
        StoreConverter idConverter = idKeyValue.getValue();
        KeyValue<Field, StoreConverter> indexKeyValue = metadata.getStoreKeyValue(name);
        Field indexField = indexKeyValue.getKey();
        StoreConverter indexConverter = indexKeyValue.getValue();
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, 0, Integer.MAX_VALUE);
        List<Document> documents = retrieve.getKey();
        Map<K, I> map = new HashMap<>();
        String idFrom = metadata.getPrimaryName();
        char idCharacter = idFrom.charAt(idFrom.length() - 1);
        idCharacter++;
        String idTo = idFrom.substring(0, idFrom.length() - 1) + idCharacter;
        String indexFrom = name;
        char indexCharacter = indexFrom.charAt(indexFrom.length() - 1);
        indexCharacter++;
        String indexTo = indexFrom.substring(0, indexFrom.length() - 1) + indexCharacter;
        for (Document document : documents) {
            NavigableMap<String, IndexableField> indexables = new TreeMap<>();
            for (IndexableField indexable : document) {
                indexables.put(indexable.name(), indexable);
            }
            Object key = idConverter.decode(context, metadata.getPrimaryName(), idField, idField.getAnnotation(LuceneStore.class), idField.getGenericType(), indexables.subMap(idFrom, true, idTo, false));
            Object value = indexConverter.decode(context, name, indexField, indexField.getAnnotation(LuceneStore.class), indexField.getGenericType(), indexables.subMap(indexFrom, true, indexTo, false));
            map.put((K) key, (I) value);
        }
        return map;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query;
        {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(name);
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, metadata.getPrimaryName(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), condition, values);
        }
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, 0, Integer.MAX_VALUE);
        List<Document> documents = retrieve.getKey();
        List<T> list = new ArrayList<>(BATCH_SIZE);
        for (Document document : documents) {
            list.add((T) metadata.decodeDocument(document));
        }
        return list;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = new MatchAllDocsQuery();
        int offset = pagination == null ? 0 : pagination.getFirst();
        int size = pagination == null ? Integer.MAX_VALUE : pagination.getSize();
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, offset, size);
        List<Document> documents = retrieve.getKey();
        List<T> list = new ArrayList<>(BATCH_SIZE);
        for (Document document : documents) {
            list.add((T) metadata.decodeDocument(document));
        }
        return list;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = null;
        BooleanQuery.Builder buffer = new BooleanQuery.Builder();
        for (Entry<String, Object> term : condition.entrySet()) {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(term.getKey());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, term.getKey(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, term.getValue());
            buffer.add(query, Occur.MUST);
        }
        query = buffer.build();
        int offset = pagination == null ? 0 : pagination.getFirst();
        int size = pagination == null ? Integer.MAX_VALUE : pagination.getSize();
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, offset, size);
        List<Document> documents = retrieve.getKey();
        List<T> list = new ArrayList<>(BATCH_SIZE);
        for (Document document : documents) {
            list.add((T) metadata.decodeDocument(document));
        }
        return list;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = null;
        BooleanQuery.Builder buffer = new BooleanQuery.Builder();
        for (Entry<String, Object> term : condition.entrySet()) {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(term.getKey());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, term.getKey(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, term.getValue());
            buffer.add(query, Occur.SHOULD);
        }
        query = buffer.build();
        int offset = pagination == null ? 0 : pagination.getFirst();
        int size = pagination == null ? Integer.MAX_VALUE : pagination.getSize();
        KeyValue<List<Document>, FloatList> retrieve = engine.retrieveDocuments(query, null, offset, size);
        List<Document> documents = retrieve.getKey();
        List<T> list = new ArrayList<>(BATCH_SIZE);
        for (Document document : documents) {
            list.add((T) metadata.decodeDocument(document));
        }
        return list;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz) {
        Query query = new MatchAllDocsQuery();
        return engine.countDocuments(query);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = null;
        BooleanQuery.Builder buffer = new BooleanQuery.Builder();
        for (Entry<String, Object> term : condition.entrySet()) {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(term.getKey());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, term.getKey(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, term.getValue());
            buffer.add(query, Occur.MUST);
        }
        query = buffer.build();
        return engine.countDocuments(query);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = null;
        BooleanQuery.Builder buffer = new BooleanQuery.Builder();
        for (Entry<String, Object> term : condition.entrySet()) {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(term.getKey());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, term.getKey(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, term.getValue());
            buffer.add(query, Occur.SHOULD);
        }
        query = buffer.build();
        return engine.countDocuments(query);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, Class<T> clazz, StoragePagination pagination) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = new MatchAllDocsQuery();
        int offset = pagination == null ? 0 : pagination.getFirst();
        int size = pagination == null ? Integer.MAX_VALUE : pagination.getSize();
        engine.iterateDocuments((document) -> {
            iterator.iterate((T) metadata.decodeDocument(document));
        }, query, null, offset, size);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = null;
        BooleanQuery.Builder buffer = new BooleanQuery.Builder();
        for (Entry<String, Object> term : condition.entrySet()) {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(term.getKey());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, term.getKey(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, term.getValue());
            buffer.add(query, Occur.MUST);
        }
        query = buffer.build();
        int offset = pagination == null ? 0 : pagination.getFirst();
        int size = pagination == null ? Integer.MAX_VALUE : pagination.getSize();
        engine.iterateDocuments((document) -> {
            iterator.iterate((T) metadata.decodeDocument(document));
        }, query, null, offset, size);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        LuceneMetadata metadata = metadatas.get(clazz);
        Query query = null;
        BooleanQuery.Builder buffer = new BooleanQuery.Builder();
        for (Entry<String, Object> term : condition.entrySet()) {
            KeyValue<Field, IndexConverter> keyValue = metadata.getIndexKeyValue(term.getKey());
            Field key = keyValue.getKey();
            IndexConverter value = keyValue.getValue();
            query = value.query(context, term.getKey(), key, key.getAnnotation(LuceneIndex.class), key.getGenericType(), StorageCondition.Equal, term.getValue());
            buffer.add(query, Occur.SHOULD);
        }
        query = buffer.build();
        int offset = pagination == null ? 0 : pagination.getFirst();
        int size = pagination == null ? Integer.MAX_VALUE : pagination.getSize();
        engine.iterateDocuments((document) -> {
            iterator.iterate((T) metadata.decodeDocument(document));
        }, query, null, offset, size);
    }

}
