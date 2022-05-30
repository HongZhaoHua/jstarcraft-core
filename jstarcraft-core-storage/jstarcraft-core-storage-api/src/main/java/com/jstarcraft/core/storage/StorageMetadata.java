package com.jstarcraft.core.storage;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * ORM元信息
 * 
 * @author Birdy
 */
public interface StorageMetadata {

    /**
     * 获取ORM的名称
     * 
     * @return
     */
    String getOrmName();

    /**
     * 获取ORM的类
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    <T extends IdentityObject> Class<T> getOrmClass();

    /**
     * 获取ORM的字段名称与类型
     * 
     * @return
     */
    Map<String, Class<?>> getFields();

    /**
     * 获取ORM的主键字段名称
     * 
     * @return
     */
    String getPrimaryName();

    /**
     * 获取ORM的主键字段类型
     * 
     * @return
     */
    <K extends Serializable> Class<K> getPrimaryClass();

    /**
     * 获取ORM的索引字段名称
     * 
     * @return
     */
    Collection<String> getIndexNames();

    /**
     * 获取ORM的版本字段名称
     * 
     * @return
     */
    String getVersionName();

}
