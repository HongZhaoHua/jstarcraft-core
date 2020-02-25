package com.jstarcraft.core.storage.lucene.converter.sort;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneSort;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.storage.lucene.converter.SortConverter;

/**
 * 对象排序转换器
 * 
 * @author Birdy
 *
 */
public class ObjectSortConverter implements SortConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneSort annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);

        try {
            // TODO 此处需要代码重构
            for (Entry<Field, SortConverter> keyValue : context.getSortKeyValues(clazz).entrySet()) {
                // TODO 此处代码可以优反射次数.
                field = keyValue.getKey();
                SortConverter converter = keyValue.getValue();
                annotation = field.getAnnotation(LuceneSort.class);
                String name = field.getName();
                for (IndexableField indexable : converter.convert(context, path + "." + name, field, annotation, field.getGenericType(), field.get(data))) {
                    indexables.add(indexable);
                }
            }
            return indexables;
        } catch (Exception exception) {
            // TODO
            throw new StorageException(exception);
        }
    }

}
