package com.jstarcraft.core.io;

import java.util.Iterator;

/**
 * 流管理器
 * 
 * @author Birdy
 *
 */
public interface StreamManager {

    /**
     * 保存流资源
     * 
     * @param key
     * @param resource
     */
    public void saveResource(String key, StreamResource resource);

    /**
     * 废弃流资源
     * 
     * @param key
     */
    public void waiveResource(String key);

    /**
     * 检测流资源
     * 
     * @param key
     * @return
     */
    public boolean haveResource(String key);

    /**
     * 遍历流资源
     * 
     * @param key
     * @return
     */
    public Iterator<StreamResource> iterateResources(String key);

    /**
     * 获取流资源
     * 
     * @param key
     * @return
     */
    public StreamResource retrieveResource(String key);

    /**
     * 遍历流信息
     * 
     * @param key
     * @return
     */
    public Iterator<StreamMetadata> iterateMetadatas(String key);

    /**
     * 获取流信息
     * 
     * @param key
     * @return
     */
    public StreamMetadata retrieveMetadata(String key);

}
