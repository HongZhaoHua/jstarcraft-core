package com.jstarcraft.core.orm.lucene.converter.sort;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.orm.exception.OrmException;
import com.jstarcraft.core.orm.lucene.annotation.LuceneSort;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.converter.SortConverter;
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
        throw new OrmException();
    }

}
