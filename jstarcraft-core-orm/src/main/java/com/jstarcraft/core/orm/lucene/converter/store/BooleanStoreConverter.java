package com.jstarcraft.core.orm.lucene.converter.store;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.NavigableMap;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.orm.lucene.annotation.SearchStore;
import com.jstarcraft.core.orm.lucene.converter.SearchContext;
import com.jstarcraft.core.orm.lucene.converter.StoreConverter;

/**
 * 布尔存储转换器
 * 
 * @author Birdy
 *
 */
public class BooleanStoreConverter implements StoreConverter {

    @Override
    public Object decode(SearchContext context, String path, Field field, SearchStore annotation, Type type, NavigableMap<String, IndexableField> indexables) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableMap<String, IndexableField> encode(SearchContext context, String path, Field field, SearchStore annotation, Type type, Object instance) {
        // TODO Auto-generated method stub
        return null;
    }

}
