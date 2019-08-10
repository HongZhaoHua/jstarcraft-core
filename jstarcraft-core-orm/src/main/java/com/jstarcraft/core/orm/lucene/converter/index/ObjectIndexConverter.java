package com.jstarcraft.core.orm.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.orm.lucene.annotation.SearchIndex;
import com.jstarcraft.core.orm.lucene.converter.IndexConverter;
import com.jstarcraft.core.orm.lucene.converter.SearchContext;
import com.jstarcraft.core.orm.lucene.exception.SearchException;
import com.jstarcraft.core.utility.KeyValue;

/**
 * 对象索引转换器
 * 
 * @author Birdy
 *
 */
public class ObjectIndexConverter implements IndexConverter {

    @Override
    public Iterable<IndexableField> convert(SearchContext context, String path, Field field, SearchIndex annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);

        try {
            // TODO 此处需要代码重构
            for (KeyValue<Field, IndexConverter> keyValue : context.getIndexKeyValues(clazz)) {
                // TODO 此处代码可以优反射次数.
                field = keyValue.getKey();
                IndexConverter converter = keyValue.getValue();
                annotation = field.getAnnotation(SearchIndex.class);
                String name = field.getName();
                for (IndexableField indexable : converter.convert(context, path + "." + name, field, annotation, field.getGenericType(), field.get(data))) {
                    indexables.add(indexable);
                }
            }
            return indexables;
        } catch (Exception exception) {
            // TODO
            throw new SearchException(exception);
        }
    }

}
