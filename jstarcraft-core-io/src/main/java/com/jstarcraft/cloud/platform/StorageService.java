package com.jstarcraft.cloud.platform;

import java.util.Iterator;

/**
 * 仓库服务
 * 
 * @author Birdy
 *
 */
public interface StorageService {

    /**
     * 创建仓库
     * 
     * @param name
     */
    public void createStorage(String name);

    /**
     * 删除仓库
     * 
     * @param name
     */
    public void deleteStorage(String name);

    /**
     * 保存资源
     * 
     * @param storage
     * @param key
     * @param resource
     */
    public void saveResource(String storage, String key, StorageResource resource);

    /**
     * 废弃资源
     * 
     * @param storage
     * @param key
     */
    public void waiveResource(String storage, String key);

    /**
     * 检测资源
     * 
     * @param storage
     * @param key
     * @return
     */
    public boolean haveResource(String storage, String key);

    public Iterator<StorageResource> iterateResources(String storage);

    /**
     * 获取资源
     * 
     * @param storage
     * @param key
     * @return
     */
    public StorageResource retrieveResource(String storage, String key);

    /**
     * 获取元信息
     * 
     * @param storage
     * @param key
     * @return
     */
    public StorageMetadata retrieveMetadata(String storage, String key);

}
