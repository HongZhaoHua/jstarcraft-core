package com.jstarcraft.core.resource.path;

import java.io.InputStream;

/**
 * 路径适配器
 * 
 * @author Birdy
 *
 */
public interface PathAdapter {

    /**
     * 根据路径获取流
     * 
     * @param path
     * @return
     */
    InputStream getStream(String path);

}
