package com.jstarcraft.core.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jstarcraft.core.cache.CacheObject;

/**
 * ORM访问器
 * 
 * @author Birdy
 *
 */
public interface OrmAccessor {

	// 基础增删查改接口部分

	/**
	 * 获取所有的Orm元信息
	 * 
	 * @return
	 */
	Collection<? extends OrmMetadata> getAllMetadata();

	/**
	 * 获取指定的对象
	 * 
	 * @param objectType
	 * @param id
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> T get(Class<T> objectType, K id);

	/**
	 * 保存指定的对象,并返回对象的主键
	 * 
	 * @param objectType
	 * @param object
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> K create(Class<T> objectType, T object);

	/**
	 * 根据主键,删除指定的对象
	 * 
	 * @param objectType
	 * @param id
	 */
	<K extends Comparable, T extends CacheObject<K>> void delete(Class<T> objectType, K id);

	/**
	 * 删除指定的对象
	 * 
	 * @param objectType
	 * @param object
	 */
	<K extends Comparable, T extends CacheObject<K>> void delete(Class<T> objectType, T object);

	/**
	 * 更新指定的对象
	 * 
	 * @param objectType
	 * @param object
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> void update(Class<T> objectType, T object);

	/**
	 * 查询指定范围的最大主键标识
	 * 
	 * @param objectType
	 * @param from
	 * @param to
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> K maximumIdentity(Class<T> objectType, K from, K to);

	/**
	 * 查询指定范围的最小主键标识
	 * 
	 * @param objectType
	 * @param from
	 * @param to
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> K minimumIdentity(Class<T> objectType, K from, K to);

	/**
	 * 查询指定索引范围的主键映射
	 * 
	 * @param objectType
	 * @param name
	 * @param values
	 * @return
	 */
	<K extends Comparable, I, T extends CacheObject<K>> Map<K, I> queryIdentities(Class<T> objectType, OrmCondition condition, String name, I... values);

	/**
	 * 查询指定索引范围的对象集合
	 * 
	 * @param objectType
	 * @param name
	 * @param values
	 * @return
	 */
	<K extends Comparable, I, T extends CacheObject<K>> List<T> queryInstances(Class<T> objectType, OrmCondition condition, String name, I... values);

	// 结构化查询接口部分

	/**
	 * 查询指定分页,返回对象的集合
	 * 
	 * @param objectType
	 * @param pagination
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> List<T> query(Class<T> objectType, OrmPagination pagination);

	/**
	 * 查询指定交集条件,返回对象的集合
	 * 
	 * @param objectType
	 * @param condition
	 * @param pagination
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> List<T> queryIntersection(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination);

	/**
	 * 查询指定并集条件,返回对象的集合
	 * 
	 * @param objectType
	 * @param condition
	 * @param pagination
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> List<T> queryUnion(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination);

	/**
	 * 查询对象总数
	 * 
	 * @param objectType
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> long count(Class<T> objectType);

	/**
	 * 查询指定交集条件的对象总数
	 * 
	 * @param objectType
	 * @param condition
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> long countIntersection(Class<T> objectType, Map<String, Object> condition);

	/**
	 * 查询指定并集条件的对象总数
	 * 
	 * @param objectType
	 * @param condition
	 * @return
	 */
	<K extends Comparable, T extends CacheObject<K>> long countUnion(Class<T> objectType, Map<String, Object> condition);

	/**
	 * 使用迭代器遍历对象
	 * 
	 * @param iterator
	 * @param objectType
	 * @param pagination
	 */
	<K extends Comparable, T extends CacheObject<K>> void iterate(OrmIterator<T> iterator, Class<T> objectType, OrmPagination pagination);

	/**
	 * 按照指定交集条件查询并使用迭代器遍历对象
	 * 
	 * @param iterator
	 * @param objectType
	 * @param condition
	 * @param pagination
	 */
	<K extends Comparable, T extends CacheObject<K>> void iterateIntersection(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination);

	/**
	 * 按照指定并集条件查询并使用迭代器遍历对象
	 * 
	 * @param iterator
	 * @param objectType
	 * @param condition
	 * @param pagination
	 */
	<K extends Comparable, T extends CacheObject<K>> void iterateUnion(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination);

}
