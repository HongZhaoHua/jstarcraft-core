package com.jstarcraft.core.resource.format;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 格式适配器
 * 
 * @author Birdy
 */
public interface FormatAdapter {

    /**
     * 遍历流得到对象
     * 
     * @param clazz
     * @param stream
     * @return
     */
    <E> Iterator<E> iterator(Class<E> clazz, InputStream stream);

}
