package com.jstarcraft.core.storage.mybatis;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCaseStrategy;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * MyBatis访问器
 * 
 * @author Birdy
 */
@Transactional
public class MyBatisAccessor implements StorageAccessor {

    /** 查询指定范围的最大主键标识 */
    private final static String MAXIMUM_ID = "MAX({})";

    /** 查询指定范围的最小主键标识 */
    private final static String MINIMUM_ID = "MIN({})";

    private SqlSessionTemplate template;

    /** MyBatis元信息 */
    protected Map<Class<? extends BaseMapper<?>>, MyBatisMetadata> metadatas = new ConcurrentHashMap<>();

    /** HQL查询语句(查询指定范围的最大主键标识),用于IdentityManager */
    private Map<Class, String> maximumIdSqls = new ConcurrentHashMap<>();

    /** HQL查询语句(查询指定范围的最小主键标识),用于IdentityManager */
    private Map<Class, String> minimumIdSqls = new ConcurrentHashMap<>();

    private StorageCaseStrategy caseStrategy;

    public synchronized StorageCaseStrategy getCaseStrategy() {
        try {
            if (caseStrategy == null) {
                // 获取表名与列名是否需要转大写或者小写?
                DatabaseMetaData metaData = template.getConnection().getMetaData();
                if (metaData.storesLowerCaseIdentifiers()) {
                    caseStrategy = StorageCaseStrategy.LOWER;
                } else if (metaData.storesUpperCaseIdentifiers()) {
                    caseStrategy = StorageCaseStrategy.UPPER;
                } else {
                    caseStrategy = StorageCaseStrategy.MIXED;
                }
            }
            return caseStrategy;
        } catch (Exception exception) {
            throw new StorageException(exception);
        }
    }

    public MyBatisAccessor(Collection<Class<?>> classes, SqlSessionTemplate template) {
        this.template = template;

        Configuration configuration = template.getConfiguration();
        for (Class clazz : classes) {
            if (!configuration.hasMapper(clazz)) {
                configuration.addMapper(clazz);
            }

            MyBatisMetadata metadata = new MyBatisMetadata(clazz);
            metadatas.put(metadata.getOrmClass(), metadata);

            String maximumIdSql = StringUtility.format(MAXIMUM_ID, metadata.getColumnName(metadata.getPrimaryName()));
            maximumIdSqls.put(metadata.getOrmClass(), maximumIdSql);

            String minimumIdSql = StringUtility.format(MINIMUM_ID, metadata.getColumnName(metadata.getPrimaryName()));
            minimumIdSqls.put(metadata.getOrmClass(), minimumIdSql);
        }
    }

    @Override
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        return (T) mapper.selectById((Serializable) id);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        return mapper.insert(object) > 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        return mapper.deleteById((Serializable) id) > 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        return mapper.deleteById((Serializable) object.getId()) > 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        return mapper.updateById(object) > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        query.select(maximumIdSqls.get(metadata.getOrmClass()));
        query.between(metadata.getColumnName(metadata.getPrimaryName()), from, to);
        List<K> values = mapper.selectObjs(query);
        return values.get(0);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        query.select(minimumIdSqls.get(metadata.getOrmClass()));
        query.between(metadata.getColumnName(metadata.getPrimaryName()), from, to);
        List<K> values = mapper.selectObjs(query);
        return values.get(0);
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        String id = metadata.getColumnName(metadata.getPrimaryName());
        String column = metadata.getColumnName(name);
        query.select(id, column);
        switch (condition) {
        case All:
            break;
        case Between:
            query.between(column, values[0], values[1]);
            break;
        case Equal:
            query.eq(column, values[0]);
            break;
        case Higher:
            query.gt(column, values[0]);
            break;
        case In:
            query.in(column, values);
            break;
        case Lower:
            query.lt(column, values[0]);
            break;
        case Unequal:
            query.ne(column, values[0]);
            break;
        }
        List<Map<String, Object>> list = mapper.selectMaps(query);
        switch (getCaseStrategy()) {
        case LOWER:
            id = id.toLowerCase();
            column = column.toLowerCase();
            break;
        case UPPER:
            id = id.toUpperCase();
            column = column.toUpperCase();
            break;
        }
        Map<K, I> map = new HashMap<>();
        for (Map<String, Object> element : list) {
            map.put((K) element.get(id), (I) element.get(column));
        }
        return map;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        String column = metadata.getColumnName(name);
        switch (condition) {
        case All:
            break;
        case Between:
            query.between(column, values[0], values[1]);
            break;
        case Equal:
            query.eq(column, values[0]);
            break;
        case Higher:
            query.gt(column, values[0]);
            break;
        case In:
            query.in(column, values);
            break;
        case Lower:
            query.lt(column, values[0]);
            break;
        case Unequal:
            query.ne(column, values[0]);
            break;
        }
        List<T> list = mapper.selectList(query);
        return list;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        if (pagination == null) {
            return mapper.selectList(query);
        } else {
            IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
            return page.getRecords();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.and((wrapper) -> {
                return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
            });
        }
        if (pagination == null) {
            return mapper.selectList(query);
        } else {
            IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
            return page.getRecords();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.or((wrapper) -> {
                return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
            });
        }
        if (pagination == null) {
            return mapper.selectList(query);
        } else {
            IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
            return page.getRecords();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        return mapper.selectCount(query);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.and((wrapper) -> {
                return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
            });
        }
        return mapper.selectCount(query);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.or((wrapper) -> {
                return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
            });
        }
        return mapper.selectCount(query);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, Class<T> clazz, StoragePagination pagination) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
        for (T object : page.getRecords()) {
            iterator.iterate(object);
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.and((wrapper) -> {
                return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
            });
        }
        IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
        for (T object : page.getRecords()) {
            iterator.iterate(object);
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        MyBatisMetadata metadata = metadatas.get(clazz);
        BaseMapper mapper = template.getMapper(metadata.getMapperClass());
        QueryWrapper<?> query = new QueryWrapper<>();
        for (Entry<String, Object> term : condition.entrySet()) {
            query.or((wrapper) -> {
                return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
            });
        }
        IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
        for (T object : page.getRecords()) {
            iterator.iterate(object);
        }
    }

}
