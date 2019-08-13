package com.jstarcraft.core.orm.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.orm.lucene.annotation.SearchIndex;
import com.jstarcraft.core.orm.lucene.converter.IndexConverter;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.exception.SearchException;
import com.jstarcraft.core.utility.ClassUtility;

/**
 * 布尔索引转换器
 * 
 * @author Birdy
 *
 */
public class BooleanIndexConverter implements IndexConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, SearchIndex annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = ClassUtility.primitiveToWrapper(clazz);
        if (AtomicBoolean.class.isAssignableFrom(clazz)) {
            indexables.add(new IntPoint(path, AtomicBoolean.class.cast(data).get() ? 1 : 0));
            return indexables;
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            indexables.add(new IntPoint(path, Boolean.class.cast(data) ? 1 : 0));
            return indexables;
        }
        throw new SearchException();
    }

}
