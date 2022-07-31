package com.jstarcraft.core.storage.lucene.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.NavigableMap;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.storage.lucene.annotation.LuceneStore;

/**
 * 存储转换器
 * 
 * @author Birdy
 *
 */
public interface StoreConverter {

    /**
     * 解码存储
     * 
     * @param context
     * @param path
     * @param field
     * @param annotation
     * @param name
     * @param type
     * @param indexables
     * @return
     */
    Object decode(LuceneContext context, String path, Field field, LuceneStore annotation, Type type, NavigableMap<String, IndexableField> indexables);

    /**
     * 编码存储
     * 
     * @param context
     * @param path
     * @param field
     * @param annotation
     * @param name
     * @param type
     * @param instance
     * @return
     */
    NavigableMap<String, IndexableField> encode(LuceneContext context, String path, Field field, LuceneStore annotation, Type type, Object instance);

}
