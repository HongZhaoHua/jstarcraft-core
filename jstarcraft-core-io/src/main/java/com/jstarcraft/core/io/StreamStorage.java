package com.jstarcraft.core.io;

import java.util.Iterator;

/**
 * 仓库服务
 * 
 * @author Birdy
 *
 */
public interface StreamStorage {

    /**
     * 创建流仓库
     * 
     * @param name
     */
    public void createStorage(String name);

    /**
     * 删除流仓库
     * 
     * @param name
     */
    public void deleteStorage(String name);

    /**
     * 保存流资源
     * 
     * @param storage
     * @param key
     * @param resource
     */
    public void saveResource(String storage, String key, StreamResource resource);

    /**
     * 废弃流资源
     * 
     * @param storage
     * @param key
     */
    public void waiveResource(String storage, String key);

    /**
     * 检测流资源
     * 
     * @param storage
     * @param key
     * @return
     */
    public boolean haveResource(String storage, String key);

    /**
     * 遍历流资源
     * 
     * @param storage
     * @return
     */
    public Iterator<StreamResource> iterateResources(String storage);

    /**
     * 获取流资源
     * 
     * @param storage
     * @param key
     * @return
     */
    public StreamResource retrieveResource(String storage, String key);
    
    /**
     * 遍历流信息
     * 
     * @param storage
     * @return
     */
    public Iterator<StreamMetadata> iterateMetadatas(String storage);

    /**
     * 获取流信息
     * 
     * @param storage
     * @param key
     * @return
     */
    public StreamMetadata retrieveMetadata(String storage, String key);

}
