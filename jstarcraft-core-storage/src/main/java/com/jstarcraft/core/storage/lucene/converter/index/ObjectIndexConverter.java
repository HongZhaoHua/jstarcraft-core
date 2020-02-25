package com.jstarcraft.core.storage.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.converter.IndexConverter;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;

/**
 * 对象索引转换器
 * 
 * @author Birdy
 *
 */
public class ObjectIndexConverter implements IndexConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneIndex annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);

        try {
            // TODO 此处需要代码重构
            for (Entry<Field, IndexConverter> keyValue : context.getIndexKeyValues(clazz).entrySet()) {
                // TODO 此处代码可以优反射次数.
                field = keyValue.getKey();
                IndexConverter converter = keyValue.getValue();
                annotation = field.getAnnotation(LuceneIndex.class);
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
