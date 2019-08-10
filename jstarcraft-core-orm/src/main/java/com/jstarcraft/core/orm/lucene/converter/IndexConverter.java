package com.jstarcraft.core.orm.lucene.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.orm.lucene.annotation.SearchIndex;

/**
 * 索引转换器
 * 
 * @author Birdy
 *
 */
public interface IndexConverter {

    /**
     * 转换索引
     * 
     * @param context
     * @param path
     * @param field
     * @param annotation
     * @param name
     * @param type
     * @param data
     * @return
     */
    Iterable<IndexableField> convert(SearchContext context, String path, Field field, SearchIndex annotation, Type type, Object data);

}
