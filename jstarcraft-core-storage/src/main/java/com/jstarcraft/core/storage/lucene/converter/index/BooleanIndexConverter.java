package com.jstarcraft.core.storage.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.converter.IndexConverter;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.utility.ClassUtility;

/**
 * 布尔索引转换器
 * 
 * @author Birdy
 *
 */
public class BooleanIndexConverter implements IndexConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneIndex annotation, Type type, Object data) {
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
        throw new StorageException();
    }

}
