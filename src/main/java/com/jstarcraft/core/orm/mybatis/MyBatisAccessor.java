package com.jstarcraft.core.orm.mybatis;

import java.io.Serializable;
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
import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmIterator;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.OrmPagination;
import com.jstarcraft.core.orm.exception.OrmQueryException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * MyBatis访问器
 * 
 * @author Birdy
 */
@Transactional
public class MyBatisAccessor implements OrmAccessor {

	/** 查询指定范围的最大主键标识 */
	private final static String MAXIMUM_ID = "MAX({})";

	/** 查询指定范围的最小主键标识 */
	private final static String MINIMUM_ID = "MIN({})";

	private SqlSessionTemplate sessionTemplate;

	/** MyBatis元信息 */
	protected Map<Class<? extends BaseMapper<?>>, MyBatisMetadata> myBatisMetadatas = new ConcurrentHashMap<>();

	/** HQL查询语句(查询指定范围的最大主键标识),用于IdentityManager */
	private Map<Class, String> maximumIdHqls = new ConcurrentHashMap<>();

	/** HQL查询语句(查询指定范围的最小主键标识),用于IdentityManager */
	private Map<Class, String> minimumIdHqls = new ConcurrentHashMap<>();

	public MyBatisAccessor(Collection<Class<?>> classes, SqlSessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;

		Configuration configuration = sessionTemplate.getConfiguration();
		for (Class clazz : classes) {
			if (!configuration.hasMapper(clazz)) {
				configuration.addMapper(clazz);
			}

			MyBatisMetadata metadata = new MyBatisMetadata(clazz);
			myBatisMetadatas.put(metadata.getOrmClass(), metadata);

			String maximumIdHql = StringUtility.format(MAXIMUM_ID, metadata.getColumnName(metadata.getPrimaryName()));
			maximumIdHqls.put(metadata.getOrmClass(), maximumIdHql);

			String minimumIdHql = StringUtility.format(MINIMUM_ID, metadata.getColumnName(metadata.getPrimaryName()));
			minimumIdHqls.put(metadata.getOrmClass(), minimumIdHql);
		}
	}

	@Override
	public Collection<? extends OrmMetadata> getAllMetadata() {
		return myBatisMetadatas.values();
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> T get(Class<T> objectType, K id) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		return (T) mapper.selectById((Serializable) id);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> K create(Class<T> objectType, T object) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		mapper.insert(object);
		return object.getId();
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void delete(Class<T> objectType, K id) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		mapper.deleteById((Serializable) id);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void delete(Class<T> objectType, T object) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		mapper.deleteById((Serializable) object.getId());
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void update(Class<T> objectType, T object) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		mapper.updateById(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K extends Comparable, T extends CacheObject<K>> K maximumIdentity(Class<T> objectType, K from, K to) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		query.select(maximumIdHqls.get(metadata.getOrmClass()));
		query.between(metadata.getColumnName(metadata.getPrimaryName()), from, to);
		List<K> values = mapper.selectObjs(query);
		return values.get(0);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> K minimumIdentity(Class<T> objectType, K from, K to) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		query.select(minimumIdHqls.get(metadata.getOrmClass()));
		query.between(metadata.getColumnName(metadata.getPrimaryName()), from, to);
		List<K> values = mapper.selectObjs(query);
		return values.get(0);
	}

	@Override
	public <K extends Comparable, I, T extends CacheObject<K>> Map<K, I> queryIdentities(Class<T> objectType, OrmCondition condition, String name, I... values) {
		if (!condition.checkValues(values)) {
			throw new OrmQueryException();
		}
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
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
		Map<K, I> map = new HashMap<>();
		for (Map<String, Object> element : list) {
			map.put((K) element.get(id.toUpperCase()), (I) element.get(column.toUpperCase()));
		}
		return map;
	}

	@Override
	public <K extends Comparable, I, T extends CacheObject<K>> List<T> queryInstances(Class<T> objectType, OrmCondition condition, String name, I... values) {
		if (!condition.checkValues(values)) {
			throw new OrmQueryException();
		}
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
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
	public <K extends Comparable, T extends CacheObject<K>> List<T> query(Class<T> objectType, OrmPagination pagination) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		if (pagination == null) {
			return mapper.selectList(query);
		} else {
			IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
			return page.getRecords();
		}
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> List<T> queryIntersection(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
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
	public <K extends Comparable, T extends CacheObject<K>> List<T> queryUnion(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
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
	public <K extends Comparable, T extends CacheObject<K>> long count(Class<T> objectType) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		return mapper.selectCount(query);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> long countIntersection(Class<T> objectType, Map<String, Object> condition) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		for (Entry<String, Object> term : condition.entrySet()) {
			query.and((wrapper) -> {
				return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
			});
		}
		return mapper.selectCount(query);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> long countUnion(Class<T> objectType, Map<String, Object> condition) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		for (Entry<String, Object> term : condition.entrySet()) {
			query.or((wrapper) -> {
				return wrapper.eq(metadata.getColumnName(term.getKey()), term.getValue());
			});
		}
		return mapper.selectCount(query);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void iterate(OrmIterator<T> iterator, Class<T> objectType, OrmPagination pagination) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
		QueryWrapper<?> query = new QueryWrapper<>();
		IPage<T> page = mapper.selectPage(new Page(pagination.getPage(), pagination.getSize()), query);
		for (T object : page.getRecords()) {
			iterator.iterate(object);
		}
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void iterateIntersection(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
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
	public <K extends Comparable, T extends CacheObject<K>> void iterateUnion(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
		MyBatisMetadata metadata = myBatisMetadatas.get(objectType);
		BaseMapper mapper = sessionTemplate.getMapper(metadata.getMapperClass());
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
