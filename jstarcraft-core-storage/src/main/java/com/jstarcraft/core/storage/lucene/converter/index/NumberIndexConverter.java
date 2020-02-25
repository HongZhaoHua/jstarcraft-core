package com.jstarcraft.core.storage.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.converter.IndexConverter;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;
import com.jstarcraft.core.utility.ClassUtility;

/**
 * 数值索引转换器
 * 
 * @author Birdy
 *
 */
public class NumberIndexConverter implements IndexConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneIndex annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = ClassUtility.primitiveToWrapper(clazz);
        if (Byte.class.isAssignableFrom(clazz)) {
            indexables.add(new IntPoint(path, (byte) data));
            return indexables;
        }
        if (Short.class.isAssignableFrom(clazz)) {
            indexables.add(new IntPoint(path, (short) data));
            return indexables;
        }
        if (Integer.class.isAssignableFrom(clazz)) {
            indexables.add(new IntPoint(path, (int) data));
            return indexables;
        }
        if (Long.class.isAssignableFrom(clazz)) {
            indexables.add(new LongPoint(path, (long) data));
            return indexables;
        }
        if (Float.class.isAssignableFrom(clazz)) {
            indexables.add(new FloatPoint(path, (float) data));
            return indexables;
        }
        if (Double.class.isAssignableFrom(clazz)) {
            indexables.add(new DoublePoint(path, (double) data));
            return indexables;
        }
        throw new StorageException();
    }

    @Override
    public Query query(LuceneContext context, String path, Field field, LuceneIndex annotation, Type type, StorageCondition condition, Object... data) {
        if (!condition.checkValues(data)) {
            throw new StorageQueryException();
        }
        Class<?> clazz = TypeUtility.getRawType(type, null);
        clazz = ClassUtility.primitiveToWrapper(clazz);
        Query query = null;
        switch (condition) {
        case All:
            query = new MatchAllDocsQuery();
            break;
        case Between:
            if (Long.class.isAssignableFrom(clazz)) {
                query = LongPoint.newRangeQuery(path, ((Number) data[0]).longValue(), ((Number) data[1]).longValue());
            } else if (Float.class.isAssignableFrom(clazz)) {
                query = FloatPoint.newRangeQuery(path, ((Number) data[0]).floatValue(), ((Number) data[1]).floatValue());
            } else if (Double.class.isAssignableFrom(clazz)) {
                query = DoublePoint.newRangeQuery(path, ((Number) data[0]).doubleValue(), ((Number) data[1]).doubleValue());
            } else {
                query = IntPoint.newRangeQuery(path, ((Number) data[0]).intValue(), ((Number) data[1]).intValue());
            }
            break;
        case Equal:
            if (Long.class.isAssignableFrom(clazz)) {
                query = LongPoint.newExactQuery(path, ((Number) data[0]).longValue());
            } else if (Float.class.isAssignableFrom(clazz)) {
                query = FloatPoint.newExactQuery(path, ((Number) data[0]).floatValue());
            } else if (Double.class.isAssignableFrom(clazz)) {
                query = DoublePoint.newExactQuery(path, ((Number) data[0]).doubleValue());
            } else {
                query = IntPoint.newExactQuery(path, ((Number) data[0]).intValue());
            }
            break;
        case Higher:
            if (Long.class.isAssignableFrom(clazz)) {
                query = LongPoint.newRangeQuery(path, ((Number) data[0]).longValue(), Long.MAX_VALUE);
            } else if (Float.class.isAssignableFrom(clazz)) {
                query = FloatPoint.newRangeQuery(path, ((Number) data[0]).floatValue(), Float.MAX_VALUE);
            } else if (Double.class.isAssignableFrom(clazz)) {
                query = DoublePoint.newRangeQuery(path, ((Number) data[0]).doubleValue(), Double.MAX_VALUE);
            } else {
                query = IntPoint.newRangeQuery(path, ((Number) data[0]).intValue() + 1, Integer.MAX_VALUE);
            }
            break;
        case In:
            if (Long.class.isAssignableFrom(clazz)) {
                long[] values = new long[data.length];
                for (int index = 0, size = data.length; index < size; index++) {
                    values[index] = ((Number) data[index]).longValue();
                }
                query = LongPoint.newSetQuery(path, values);
            } else if (Float.class.isAssignableFrom(clazz)) {
                float[] values = new float[data.length];
                for (int index = 0, size = data.length; index < size; index++) {
                    values[index] = ((Number) data[index]).floatValue();
                }
                query = FloatPoint.newSetQuery(path, values);
            } else if (Double.class.isAssignableFrom(clazz)) {
                double[] values = new double[data.length];
                for (int index = 0, size = data.length; index < size; index++) {
                    values[index] = ((Number) data[index]).doubleValue();
                }
                query = DoublePoint.newSetQuery(path, values);
            } else {
                int[] values = new int[data.length];
                for (int index = 0, size = data.length; index < size; index++) {
                    values[index] = ((Number) data[index]).intValue();
                }
                query = IntPoint.newSetQuery(path, values);
            }
            break;
        case Lower:
            if (Long.class.isAssignableFrom(clazz)) {
                query = LongPoint.newRangeQuery(path, Integer.MIN_VALUE, ((Number) data[0]).longValue());
            } else if (Float.class.isAssignableFrom(clazz)) {
                query = FloatPoint.newRangeQuery(path, Integer.MIN_VALUE, ((Number) data[0]).floatValue());
            } else if (Double.class.isAssignableFrom(clazz)) {
                query = DoublePoint.newRangeQuery(path, Integer.MIN_VALUE, ((Number) data[0]).doubleValue());
            } else {
                query = IntPoint.newRangeQuery(path, Integer.MIN_VALUE, ((Number) data[0]).intValue() - 1);
            }
            break;
        case Unequal:
            if (Long.class.isAssignableFrom(clazz)) {
                query = LongPoint.newExactQuery(path, ((Number) data[0]).longValue());
            } else if (Float.class.isAssignableFrom(clazz)) {
                query = FloatPoint.newExactQuery(path, ((Number) data[0]).floatValue());
            } else if (Double.class.isAssignableFrom(clazz)) {
                query = DoublePoint.newExactQuery(path, ((Number) data[0]).doubleValue());
            } else {
                query = IntPoint.newExactQuery(path, ((Number) data[0]).intValue());
            }
            query = new BooleanQuery.Builder().add(query, Occur.MUST_NOT).build();
            break;
        default:
            throw new UnsupportedOperationException();
        }
        return query;
    }

}
