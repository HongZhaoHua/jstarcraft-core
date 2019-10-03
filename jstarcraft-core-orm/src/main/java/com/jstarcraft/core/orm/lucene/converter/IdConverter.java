package com.jstarcraft.core.orm.lucene.converter;

/**
 * 标识转换器
 * 
 * @author Birdy
 *
 */
public interface IdConverter {

    /**
     * 解码标识
     * 
     * @param data
     * @return
     */
    Object decode(String data);

    /**
     * 编码标识
     * 
     * @param id
     * @return
     */
    String encode(Object id);
}
