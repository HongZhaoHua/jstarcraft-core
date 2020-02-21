package com.jstarcraft.core.storage.identification;

/**
 * 标识工厂
 * 
 * @author Birdy
 *
 */
public interface IdentityFactory {

    /**
     * 获取标识定义
     * 
     * @return
     */
    IdentityDefinition getDefinition();

    /**
     * 获取标识分区
     * 
     * @return
     */
    int getPartition();

    /**
     * 根据标识序列
     * 
     * @param clazz
     * @param partition
     * @return
     */
    long getSequence();

}
