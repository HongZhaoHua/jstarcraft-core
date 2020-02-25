package com.jstarcraft.core.storage.lucene.converter.store;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneStore;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.storage.lucene.converter.StoreConverter;

/**
 * 集合存储转换器
 * 
 * @author Birdy
 *
 */
public class CollectionStoreConverter implements StoreConverter {

    @Override
    public Object decode(LuceneContext context, String path, Field field, LuceneStore annotation, Type type, NavigableMap<String, IndexableField> indexables) {
        String from = path;
        char character = path.charAt(path.length() - 1);
        character++;
        String to = path.substring(0, path.length() - 1) + character;
        indexables = indexables.subMap(from, true, to, false);
        Class<?> clazz = TypeUtility.getRawType(type, null);
        // 兼容UniMi
        type = TypeUtility.refineType(type, Collection.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        Type elementType = types[0];
        Class<?> elementClazz = TypeUtility.getRawType(elementType, null);

        try {
            // TODO 此处需要代码重构
            Collection<Object> collection = (Collection) context.getInstance(clazz);
            Specification specification = Specification.getSpecification(elementClazz);
            StoreConverter converter = context.getStoreConverter(specification);

            IndexableField indexable = indexables.get(path + ".size");
            int size = indexable.numericValue().intValue();
            for (int index = 0; index < size; index++) {
                Object element = converter.decode(context, path + "[" + index + "]", field, annotation, elementType, indexables);
                collection.add(element);
            }
            return collection;
        } catch (Exception exception) {
            // TODO
            throw new StorageException(exception);
        }
    }

    @Override
    public NavigableMap<String, IndexableField> encode(LuceneContext context, String path, Field field, LuceneStore annotation, Type type, Object instance) {
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        // 兼容UniMi
        type = TypeUtility.refineType(type, Collection.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        Type elementType = types[0];
        Class<?> elementClazz = TypeUtility.getRawType(elementType, null);

        try {
            // TODO 此处需要代码重构
            Collection<?> collection = Collection.class.cast(instance);
            Specification specification = Specification.getSpecification(elementClazz);
            StoreConverter converter = context.getStoreConverter(specification);

            int size = collection.size();
            IndexableField indexable = new StoredField(path + ".size", size);
            indexables.put(path + ".size", indexable);
            int index = 0;
            for (Object element : collection) {
                indexables.putAll(converter.encode(context, path + "[" + index + "]", field, annotation, elementType, element));
                index++;
            }
            return indexables;
        } catch (Exception exception) {
            // TODO
            throw new StorageException(exception);
        }
    }

}
