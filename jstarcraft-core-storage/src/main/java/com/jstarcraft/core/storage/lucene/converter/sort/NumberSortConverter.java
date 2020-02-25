package com.jstarcraft.core.storage.lucene.converter.sort;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneSort;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.storage.lucene.converter.SortConverter;
import com.jstarcraft.core.utility.ClassUtility;

/**
 * 数值排序转换器
 * 
 * @author Birdy
 *
 */
public class NumberSortConverter implements SortConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneSort annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = ClassUtility.primitiveToWrapper(clazz);
        if (Byte.class.isAssignableFrom(clazz)) {
            indexables.add(new NumericDocValuesField(path, (byte) data));
            return indexables;
        }
        if (Short.class.isAssignableFrom(clazz)) {
            indexables.add(new NumericDocValuesField(path, (short) data));
            return indexables;
        }
        if (Integer.class.isAssignableFrom(clazz)) {
            indexables.add(new NumericDocValuesField(path, (int) data));
            return indexables;
        }
        if (Long.class.isAssignableFrom(clazz)) {
            indexables.add(new NumericDocValuesField(path, (long) data));
            return indexables;
        }
        if (Float.class.isAssignableFrom(clazz)) {
            indexables.add(new FloatDocValuesField(path, (float) data));
            return indexables;
        }
        if (Double.class.isAssignableFrom(clazz)) {
            indexables.add(new DoubleDocValuesField(path, (double) data));
            return indexables;
        }
        throw new StorageException();
    }

    @Override
    public Sort sort(LuceneContext context, String path, Field field, LuceneSort annotation, Type type, boolean scend) {
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = ClassUtility.primitiveToWrapper(clazz);
        Sort sort = null;
        if (Long.class.isAssignableFrom(clazz)) {
            sort = new Sort(new SortField(path, SortField.Type.LONG, !scend));
        } else if (Float.class.isAssignableFrom(clazz)) {
            sort = new Sort(new SortField(path, SortField.Type.FLOAT, !scend));
        } else if (Double.class.isAssignableFrom(clazz)) {
            sort = new Sort(new SortField(path, SortField.Type.DOUBLE, !scend));
        } else {
            sort = new Sort(new SortField(path, SortField.Type.INT, !scend));
        }
        return sort;
    }

}
