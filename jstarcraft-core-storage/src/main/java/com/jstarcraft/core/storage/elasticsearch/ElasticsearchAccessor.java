package com.jstarcraft.core.storage.elasticsearch;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactory;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.ConditionType;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.exception.StorageException;

/**
 * Elasticsearch访问器
 * 
 * @author Birdy
 *
 */
public class ElasticsearchAccessor implements StorageAccessor {

    /** 元数据集合 */
    private HashMap<Class<?>, ElasticsearchMetadata> metadatas = new HashMap<>();
    /** 仓储映射 */
    private Map<Class<?>, SimpleElasticsearchRepository> repositories = new HashMap<>();

    private ElasticsearchOperations template;

    public ElasticsearchAccessor(Collection<Class<?>> classes, ElasticsearchOperations template) {
        this.template = template;
        ElasticsearchRepositoryFactory factory = new ElasticsearchRepositoryFactory(template);
        for (Class<?> ormClass : classes) {
            ElasticsearchMetadata metadata = new ElasticsearchMetadata(ormClass);
            metadatas.put(ormClass, metadata);
            ElasticsearchEntityInformation<?, Long> information = factory.getEntityInformation(ormClass);
            SimpleElasticsearchRepository<?, Long> repository = new SimpleElasticsearchRepository<>(information, template);
            repositories.put(ormClass, repository);
        }
    }

    @Override
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        Optional<T> optional = repository.findById(id);
        return optional.isPresent() ? optional.get() : null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        try {
            repository.save(object);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        try {
            repository.delete(object);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        try {
            repository.save(object);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        ElasticsearchMetadata metadata = metadatas.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withPageable(PageRequest.of(0, 1));
        builder.withSort(SortBuilders.fieldSort(metadata.getPrimaryName()).order(SortOrder.DESC));
        Page<T> page = repository.search(builder.build());
        return page.getSize() == 0 ? null : page.getContent().get(0).getId();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        ElasticsearchMetadata metadata = metadatas.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withPageable(PageRequest.of(0, 1));
        builder.withSort(SortBuilders.fieldSort(metadata.getPrimaryName()).order(SortOrder.ASC));
        Page<T> page = repository.search(builder.build());
        return page.getSize() == 0 ? null : page.getContent().get(0).getId();
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, String name, StorageCondition<I> condition) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withSourceFilter(new FetchSourceFilter(new String[] { name }, null));
        ConditionType type = condition.getType();
        I[] values = condition.getValues();
        switch (type) {
        case All:
            builder.withQuery(QueryBuilders.matchAllQuery());
            break;
        case Between:
            builder.withQuery(QueryBuilders.rangeQuery(name).from(values[0], true).to(values[1], true));
            break;
        case Equal:
            builder.withQuery(QueryBuilders.termQuery(name, values[0]));
            break;
        case Higher:
            builder.withQuery(QueryBuilders.rangeQuery(name).from(values[0], false));
            break;
        case In:
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for (Object value : values) {
                query.should(QueryBuilders.termQuery(name, value));
            }
            builder.withQuery(query);
            break;
        case Lower:
            builder.withQuery(QueryBuilders.rangeQuery(name).to(values[1], false));
            break;
        case Unequal:
            builder.withQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(name, values[0])));
            break;
        }
        // TODO 此处分页大小取决于Elasticsearch配置
        builder.withPageable(PageRequest.of(0, 10000));
        Page<T> page = repository.search(builder.build());
        Map<K, I> map = new HashMap<>();
        try {
            Field field = ReflectionUtility.getField(clazz, name);
            for (T instance : page) {
                map.put(instance.getId(), (I) field.get(instance));
            }
        } catch (Exception exception) {
            throw new StorageException(exception);
        }
        return map;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, String name, StorageCondition<I> condition) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withSourceFilter(new FetchSourceFilter(new String[] { name }, null));
        ConditionType type = condition.getType();
        I[] values = condition.getValues();
        switch (type) {
        case All:
            builder.withQuery(QueryBuilders.matchAllQuery());
            break;
        case Between:
            builder.withQuery(QueryBuilders.rangeQuery(name).from(values[0], true).to(values[1], true));
            break;
        case Equal:
            builder.withQuery(QueryBuilders.termQuery(name, values[0]));
            break;
        case Higher:
            builder.withQuery(QueryBuilders.rangeQuery(name).from(values[0], false));
            break;
        case In:
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for (Object value : values) {
                query.should(QueryBuilders.termQuery(name, value));
            }
            builder.withQuery(query);
            break;
        case Lower:
            builder.withQuery(QueryBuilders.rangeQuery(name).to(values[1], false));
            break;
        case Unequal:
            builder.withQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(name, values[0])));
            break;
        }
        // TODO 此处分页大小取决于Elasticsearch配置
        builder.withPageable(PageRequest.of(0, 10000));
        Page<T> page = repository.search(builder.build());
        return page.getContent();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (pagination != null) {
            builder.withPageable(PageRequest.of(pagination.getPage() - 1, pagination.getSize()));
        }
        builder.withQuery(QueryBuilders.matchAllQuery());
        Page<T> page = repository.search(builder.build());
        return page.getContent();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (pagination != null) {
            builder.withPageable(PageRequest.of(pagination.getPage() - 1, pagination.getSize()));
        }
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.must(QueryBuilders.termQuery(term.getKey(), term.getValue()));
        }
        builder.withQuery(query);
        Page<T> page = repository.search(builder.build());
        return page.getContent();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (pagination != null) {
            builder.withPageable(PageRequest.of(pagination.getPage() - 1, pagination.getSize()));
        }
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.should(QueryBuilders.termQuery(term.getKey(), term.getValue()));
        }
        builder.withQuery(query);
        Page<T> page = repository.search(builder.build());
        return page.getContent();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        return repository.count();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.must(QueryBuilders.termQuery(term.getKey(), term.getValue()));
        }
        builder.withQuery(query);
        return template.count(builder.build(), clazz, template.getIndexCoordinatesFor(clazz));
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        SimpleElasticsearchRepository<T, K> repository = repositories.get(clazz);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.should(QueryBuilders.termQuery(term.getKey(), term.getValue()));
        }
        builder.withQuery(query);
        return template.count(builder.build(), clazz, template.getIndexCoordinatesFor(clazz));
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, Class<T> clazz, StoragePagination pagination) {
        for (T instance : queryInstances(clazz, pagination)) {
            iterator.iterate(instance);
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        for (T instance : queryIntersection(clazz, condition, pagination)) {
            iterator.iterate(instance);
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        for (T instance : queryUnion(clazz, condition, pagination)) {
            iterator.iterate(instance);
        }
    }

}
