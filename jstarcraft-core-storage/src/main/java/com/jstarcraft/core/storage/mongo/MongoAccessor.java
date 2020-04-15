package com.jstarcraft.core.storage.mongo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.CloseableIterator;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * Mongo访问器
 * 
 * @author Birdy
 *
 */
public class MongoAccessor implements StorageAccessor {

    /** 元数据集合 */
    private HashMap<Class<?>, MongoMetadata> metadatas = new HashMap<>();

    private MongoTemplate template;

    public MongoAccessor(Collection<Class<?>> classes, MongoTemplate template) {
        this.template = template;

        for (Class<?> ormClass : classes) {
            MongoMetadata metadata = new MongoMetadata(ormClass);
            metadatas.put(ormClass, metadata);
        }
    }

    @Override
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        MongoMetadata metadata = metadatas.get(clazz);
        return template.findById(id, clazz, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        MongoMetadata metadata = metadatas.get(clazz);
        try {
            template.insert(object, metadata.getOrmName());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        MongoMetadata metadata = metadatas.get(clazz);
        DeleteResult state = template.remove(Query.query(Criteria.where(MongoMetadata.mongoId).is(id)), metadata.getOrmName());
        return state.getDeletedCount() > 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        MongoMetadata metadata = metadatas.get(clazz);
        DeleteResult state = template.remove(object, metadata.getOrmName());
        return state.getDeletedCount() > 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
        MongoMetadata metadata = metadatas.get(clazz);
        try {
            template.save(object, metadata.getOrmName());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        MongoMetadata metadata = metadatas.get(clazz);
        Query query = Query.query(Criteria.where(MongoMetadata.mongoId).gte(from).lte(to));
        query.fields().include(MongoMetadata.mongoId);
        query.with(Sort.by(Direction.DESC, MongoMetadata.mongoId));
        T instance = template.findOne(query, clazz, metadata.getOrmName());
        return instance.getId();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        MongoMetadata metadata = metadatas.get(clazz);
        Query query = Query.query(Criteria.where(MongoMetadata.mongoId).gte(from).lte(to));
        query.fields().include(MongoMetadata.mongoId);
        query.with(Sort.by(Direction.ASC, MongoMetadata.mongoId));
        T instance = template.findOne(query, clazz, metadata.getOrmName());
        return instance.getId();
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        MongoMetadata metadata = metadatas.get(clazz);
        if (metadata.getPrimaryName().equals(name)) {
            name = MongoMetadata.mongoId;
        }
        // TODO 此处存在问题(需要处理values)
        Query query = null;
        switch (condition) {
        case All:
            query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
            break;
        case Between:
            query = Query.query(Criteria.where(name).gte(values[0]).lte(values[1]));
            break;
        case Equal:
            query = Query.query(Criteria.where(name).is(values[0]));
            break;
        case Higher:
            query = Query.query(Criteria.where(name).gt(values[0]));
            break;
        case In:
            query = Query.query(Criteria.where(name).in(values));
            break;
        case Lower:
            query = Query.query(Criteria.where(name).lt(values[0]));
            break;
        case Unequal:
            query = Query.query(Criteria.where(name).ne(values[0]));
            break;
        }
        query.fields().include(MongoMetadata.mongoId).include(name);
        Map<K, I> map = new HashMap<>();
        try (MongoCursor<Document> cursor = template.getCollection(metadata.getOrmName()).find(query.getQueryObject()).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                map.put((K) document.get(MongoMetadata.mongoId), (I) document.get(name));
            }
        }
        return map;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        MongoMetadata metadata = metadatas.get(clazz);
        if (metadata.getPrimaryName().equals(name)) {
            name = MongoMetadata.mongoId;
        }
        // TODO 此处存在问题(需要处理values)
        Query query = null;
        switch (condition) {
        case All:
            query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
            break;
        case Between:
            query = Query.query(Criteria.where(name).gte(values[0]).lte(values[1]));
            break;
        case Equal:
            query = Query.query(Criteria.where(name).is(values[0]));
            break;
        case Higher:
            query = Query.query(Criteria.where(name).gt(values[0]));
            break;
        case In:
            query = Query.query(Criteria.where(name).in(values));
            break;
        case Lower:
            query = Query.query(Criteria.where(name).lt(values[0]));
            break;
        case Unequal:
            query = Query.query(Criteria.where(name).ne(values[0]));
            break;
        }
        return template.find(query, clazz, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        MongoMetadata metadata = metadatas.get(clazz);
        Query query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        return template.find(query, clazz, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MongoMetadata metadata = metadatas.get(clazz);
        final Iterator<Entry<String, Object>> conditionIterator = condition.entrySet().iterator();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).exists(true);
        Criteria[] andCriterias = new Criteria[condition.size()];
        int index = 0;
        while (conditionIterator.hasNext()) {
            Entry<String, Object> keyValue = conditionIterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            if (metadata.getPrimaryName().equals(key)) {
                key = MongoMetadata.mongoId;
            }
            andCriterias[index++] = Criteria.where(key).is(value);
        }
        Query query = Query.query(criteria.andOperator(andCriterias));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        return template.find(query, clazz, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MongoMetadata metadata = metadatas.get(clazz);
        final Iterator<Entry<String, Object>> conditionIterator = condition.entrySet().iterator();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).exists(true);
        Criteria[] orCriterias = new Criteria[condition.size()];
        int index = 0;
        while (conditionIterator.hasNext()) {
            Entry<String, Object> keyValue = conditionIterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            if (metadata.getPrimaryName().equals(key)) {
                key = MongoMetadata.mongoId;
            }
            orCriterias[index++] = Criteria.where(key).is(value);
        }
        Query query = Query.query(criteria.orOperator(orCriterias));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        return template.find(query, clazz, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz) {
        MongoMetadata metadata = metadatas.get(clazz);
        Query query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
        return template.count(query, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        MongoMetadata metadata = metadatas.get(clazz);
        final Iterator<Entry<String, Object>> conditionIterator = condition.entrySet().iterator();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).exists(true);
        Criteria[] andCriterias = new Criteria[condition.size()];
        int index = 0;
        while (conditionIterator.hasNext()) {
            Entry<String, Object> keyValue = conditionIterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            if (metadata.getPrimaryName().equals(key)) {
                key = MongoMetadata.mongoId;
            }
            andCriterias[index++] = Criteria.where(key).is(value);
        }
        Query query = Query.query(criteria.andOperator(andCriterias));
        return template.count(query, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        MongoMetadata metadata = metadatas.get(clazz);
        final Iterator<Entry<String, Object>> conditionIterator = condition.entrySet().iterator();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).exists(true);
        Criteria[] orCriterias = new Criteria[condition.size()];
        int index = 0;
        while (conditionIterator.hasNext()) {
            Entry<String, Object> keyValue = conditionIterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            if (metadata.getPrimaryName().equals(key)) {
                key = MongoMetadata.mongoId;
            }
            orCriterias[index++] = Criteria.where(key).is(value);
        }
        Query query = Query.query(criteria.orOperator(orCriterias));
        return template.count(query, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, Class<T> clazz, StoragePagination pagination) {
        MongoMetadata metadata = metadatas.get(clazz);
        Query query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        try (CloseableIterator<T> stream = template.stream(query, clazz, metadata.getOrmName())) {
            while (stream.hasNext()) {
                try {
                    // TODO 需要考虑中断
                    final T object = stream.next();
                    iterator.iterate(object);
                } catch (Throwable throwable) {
                    throw new StorageQueryException(throwable);
                }
            }
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MongoMetadata metadata = metadatas.get(clazz);
        final Iterator<Entry<String, Object>> conditionIterator = condition.entrySet().iterator();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).exists(true);
        Criteria[] andCriterias = new Criteria[condition.size()];
        int index = 0;
        while (conditionIterator.hasNext()) {
            Entry<String, Object> keyValue = conditionIterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            if (metadata.getPrimaryName().equals(key)) {
                key = MongoMetadata.mongoId;
            }
            andCriterias[index++] = Criteria.where(key).is(value);
        }
        Query query = Query.query(criteria.andOperator(andCriterias));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        try (CloseableIterator<T> stream = template.stream(query, clazz, metadata.getOrmName())) {
            while (stream.hasNext()) {
                try {
                    // TODO 需要考虑中断
                    final T object = stream.next();
                    iterator.iterate(object);
                } catch (Throwable throwable) {
                    throw new StorageQueryException(throwable);
                }
            }
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MongoMetadata metadata = metadatas.get(clazz);
        final Iterator<Entry<String, Object>> conditionIterator = condition.entrySet().iterator();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).exists(true);
        Criteria[] orCriterias = new Criteria[condition.size()];
        int index = 0;
        while (conditionIterator.hasNext()) {
            Entry<String, Object> keyValue = conditionIterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            if (metadata.getPrimaryName().equals(key)) {
                key = MongoMetadata.mongoId;
            }
            orCriterias[index++] = Criteria.where(key).is(value);
        }
        Query query = Query.query(criteria.orOperator(orCriterias));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        try (CloseableIterator<T> stream = template.stream(query, clazz, metadata.getOrmName())) {
            while (stream.hasNext()) {
                try {
                    // TODO 需要考虑中断
                    final T object = stream.next();
                    iterator.iterate(object);
                } catch (Throwable throwable) {
                    throw new StorageQueryException(throwable);
                }
            }
        }
    }

    public <K extends Comparable, T extends IdentityObject<K>> long update(Class<T> clazz, Query query, Update update) {
        MongoMetadata metadata = metadatas.get(clazz);
        UpdateResult count = template.updateMulti(query, update, metadata.getOrmName());
        return count.getModifiedCount();
    }

}
