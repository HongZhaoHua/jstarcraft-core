package com.jstarcraft.core.io;

import java.io.InputStream;
import java.util.Iterator;

import com.jstarcraft.core.utility.KeyValue;

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
     * @param path
     * @param stream
     */
    public void saveResource(String path, InputStream stream);

    /**
     * 废弃流资源
     * 
     * @param path
     */
    public void waiveResource(String path);

    /**
     * 检测流资源
     * 
     * @param path
     * @return
     */
    public boolean haveResource(String path);

    /**
     * 遍历流资源
     * 
     * @param path
     * @return
     */
    public Iterator<KeyValue<String, InputStream>> iterateResources(String path);

    /**
     * 获取流资源
     * 
     * @param path
     * @return
     */
    public InputStream retrieveResource(String path);

}
