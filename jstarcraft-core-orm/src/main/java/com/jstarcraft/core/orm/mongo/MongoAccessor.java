package com.jstarcraft.core.orm.mongo;

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
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmIterator;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.OrmPagination;
import com.jstarcraft.core.orm.exception.OrmQueryException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;

/**
 * Mongo访问器
 * 
 * @author Birdy
 *
 */
public class MongoAccessor implements OrmAccessor {

    /** 元数据集合 */
    private HashMap<Class<?>, MongoMetadata> metadatas = new HashMap<>();

    private MongoTemplate mongoTemplate;

    public MongoAccessor(Collection<Class<?>> classes, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;

        for (Class<?> ormClass : classes) {
            MongoMetadata metadata = new MongoMetadata(ormClass);
            metadatas.put(ormClass, metadata);
        }
    }

    @Override
    public Collection<? extends OrmMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T get(Class<T> objectType, K id) {
        MongoMetadata metadata = metadatas.get(objectType);
        return mongoTemplate.findById(id, objectType, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K create(Class<T> objectType, T object) {
        MongoMetadata metadata = metadatas.get(objectType);
        mongoTemplate.insert(object, metadata.getOrmName());
        return object.getId();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void delete(Class<T> objectType, K id) {
        MongoMetadata metadata = metadatas.get(objectType);
        mongoTemplate.remove(Query.query(Criteria.where(MongoMetadata.mongoId).is(id)), metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void delete(Class<T> objectType, T object) {
        MongoMetadata metadata = metadatas.get(objectType);
        mongoTemplate.remove(object, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void update(Class<T> objectType, T object) {
        MongoMetadata metadata = metadatas.get(objectType);
        mongoTemplate.save(object, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> objectType, K from, K to) {
        MongoMetadata metadata = metadatas.get(objectType);
        Query query = Query.query(Criteria.where(MongoMetadata.mongoId).gte(from).lte(to));
        query.fields().include(MongoMetadata.mongoId);
        query.with(Sort.by(Direction.DESC, MongoMetadata.mongoId));
        T instance = mongoTemplate.findOne(query, objectType, metadata.getOrmName());
        return instance.getId();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> objectType, K from, K to) {
        MongoMetadata metadata = metadatas.get(objectType);
        Query query = Query.query(Criteria.where(MongoMetadata.mongoId).gte(from).lte(to));
        query.fields().include(MongoMetadata.mongoId);
        query.with(Sort.by(Direction.ASC, MongoMetadata.mongoId));
        T instance = mongoTemplate.findOne(query, objectType, metadata.getOrmName());
        return instance.getId();
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> objectType, OrmCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new OrmQueryException();
        }
        MongoMetadata metadata = metadatas.get(objectType);
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
        try (MongoCursor<Document> cursor = mongoTemplate.getCollection(metadata.getOrmName()).find(query.getQueryObject()).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                map.put((K) document.get(MongoMetadata.mongoId), (I) document.get(name));
            }
        }
        return map;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> objectType, OrmCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new OrmQueryException();
        }
        MongoMetadata metadata = metadatas.get(objectType);
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
        return mongoTemplate.find(query, objectType, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> query(Class<T> objectType, OrmPagination pagination) {
        MongoMetadata metadata = metadatas.get(objectType);
        Query query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        return mongoTemplate.find(query, objectType, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        MongoMetadata metadata = metadatas.get(objectType);
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
        return mongoTemplate.find(query, objectType, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        MongoMetadata metadata = metadatas.get(objectType);
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
        return mongoTemplate.find(query, objectType, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long count(Class<T> objectType) {
        MongoMetadata metadata = metadatas.get(objectType);
        Query query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
        return mongoTemplate.count(query, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> objectType, Map<String, Object> condition) {
        MongoMetadata metadata = metadatas.get(objectType);
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
        return mongoTemplate.count(query, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> objectType, Map<String, Object> condition) {
        MongoMetadata metadata = metadatas.get(objectType);
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
        return mongoTemplate.count(query, metadata.getOrmName());
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(OrmIterator<T> iterator, Class<T> objectType, OrmPagination pagination) {
        MongoMetadata metadata = metadatas.get(objectType);
        Query query = new Query(Criteria.where(MongoMetadata.mongoId).exists(true));
        if (pagination != null) {
            query.skip(pagination.getFirst());
            query.limit(pagination.getSize());
        }
        try (CloseableIterator<T> stream = mongoTemplate.stream(query, objectType, metadata.getOrmName())) {
            while (stream.hasNext()) {
                try {
                    // TODO 需要考虑中断
                    final T object = stream.next();
                    iterator.iterate(object);
                } catch (Throwable throwable) {
                    throw new OrmQueryException(throwable);
                }
            }
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        MongoMetadata metadata = metadatas.get(objectType);
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
        try (CloseableIterator<T> stream = mongoTemplate.stream(query, objectType, metadata.getOrmName())) {
            while (stream.hasNext()) {
                try {
                    // TODO 需要考虑中断
                    final T object = stream.next();
                    iterator.iterate(object);
                } catch (Throwable throwable) {
                    throw new OrmQueryException(throwable);
                }
            }
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        MongoMetadata metadata = metadatas.get(objectType);
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
        try (CloseableIterator<T> stream = mongoTemplate.stream(query, objectType, metadata.getOrmName())) {
            while (stream.hasNext()) {
                try {
                    // TODO 需要考虑中断
                    final T object = stream.next();
                    iterator.iterate(object);
                } catch (Throwable throwable) {
                    throw new OrmQueryException(throwable);
                }
            }
        }
    }

    public <K extends Comparable, T extends IdentityObject<K>> long update(Class<T> objectType, Query query, Update update) {
        MongoMetadata metadata = metadatas.get(objectType);
        UpdateResult count = mongoTemplate.updateMulti(query, update, metadata.getOrmName());
        return count.getModifiedCount();
    }

}
