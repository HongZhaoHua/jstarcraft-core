package com.jstarcraft.core.storage.lucene.converter;

import java.lang.reflect.Type;

/**
 * 标识转换器
 * 
 * @author Birdy
 *
 */
public interface IdConverter {

    /**
     * 转换标识
     * 
     * @param type
     * @param id
     * @return
     */
    String convert(Type type, Object id);
}
