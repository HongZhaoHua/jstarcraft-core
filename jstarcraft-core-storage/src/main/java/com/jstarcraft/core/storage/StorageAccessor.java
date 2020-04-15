package com.jstarcraft.core.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * ORM访问器
 * 
 * @author Birdy
 *
 */
public interface StorageAccessor {

    // 基础增删查改接口部分

    /**
     * 获取所有的Orm元信息
     * 
     * @return
     */
    Collection<? extends StorageMetadata> getAllMetadata();

    /**
     * 获取指定的对象
     * 
     * @param clazz
     * @param id
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id);

    /**
     * 保存指定的对象,并返回对象的主键
     * 
     * @param clazz
     * @param object
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object);

    /**
     * 根据主键,删除指定的对象
     * 
     * @param clazz
     * @param id
     */
    <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id);

    /**
     * 删除指定的对象
     * 
     * @param clazz
     * @param object
     */
    <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object);

    /**
     * 更新指定的对象
     * 
     * @param clazz
     * @param object
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object);

    /**
     * 查询指定范围的最大主键标识
     * 
     * @param clazz
     * @param from
     * @param to
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to);

    /**
     * 查询指定范围的最小主键标识
     * 
     * @param clazz
     * @param from
     * @param to
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to);

    /**
     * 查询指定索引范围的主键映射
     * 
     * @param clazz
     * @param name
     * @param values
     * @return
     */
    <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values);

    /**
     * 查询指定索引范围的对象集合
     * 
     * @param clazz
     * @param name
     * @param values
     * @return
     */
    <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values);

    // 结构化查询接口部分

    /**
     * 查询指定分页,返回对象的集合
     * 
     * @param clazz
     * @param pagination
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination);

    /**
     * 查询指定交集条件,返回对象的集合
     * 
     * @param clazz
     * @param condition
     * @param pagination
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination);

    /**
     * 查询指定并集条件,返回对象的集合
     * 
     * @param clazz
     * @param condition
     * @param pagination
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination);

    /**
     * 查询对象总数
     * 
     * @param clazz
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz);

    /**
     * 查询指定交集条件的对象总数
     * 
     * @param clazz
     * @param condition
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition);

    /**
     * 查询指定并集条件的对象总数
     * 
     * @param clazz
     * @param condition
     * @return
     */
    <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition);

    /**
     * 使用迭代器遍历对象
     * 
     * @param iterator
     * @param clazz
     * @param pagination
     */
    <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, Class<T> clazz, StoragePagination pagination);

    /**
     * 按照指定交集条件查询并使用迭代器遍历对象
     * 
     * @param iterator
     * @param clazz
     * @param condition
     * @param pagination
     */
    <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination);

    /**
     * 按照指定并集条件查询并使用迭代器遍历对象
     * 
     * @param iterator
     * @param clazz
     * @param condition
     * @param pagination
     */
    <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination);

}
