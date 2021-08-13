package com.jstarcraft.core.storage.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.converter.IndexConverter;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;

/**
 * 集合索引转换器
 * 
 * @author Birdy
 *
 */
public class CollectionIndexConverter implements IndexConverter {

    private int[] booleans2Primitives(Collection<Boolean> oldArray) {
        int size = oldArray.size();
        int[] newArray = new int[size];
        int index = 0;
        for (Boolean element : oldArray) {
            newArray[index++] = element ? 1 : 0;
        }
        return newArray;
    }

    private int[] bytes2Primitives(Collection<Byte> oldArray) {
        int size = oldArray.size();
        int[] newArray = new int[size];
        int index = 0;
        for (Byte element : oldArray) {
            newArray[index++] = element.intValue();
        }
        return newArray;
    }

    private int[] shorts2Primitives(Collection<Short> oldArray) {
        int size = oldArray.size();
        int[] newArray = new int[size];
        int index = 0;
        for (Short element : oldArray) {
            newArray[index++] = element.intValue();
        }
        return newArray;
    }

    private int[] integers2Primitives(Collection<Integer> oldArray) {
        int size = oldArray.size();
        int[] newArray = new int[size];
        int index = 0;
        for (Integer element : oldArray) {
            newArray[index++] = element.intValue();
        }
        return newArray;
    }

    private long[] longs2Primitives(Collection<Long> oldArray) {
        int size = oldArray.size();
        long[] newArray = new long[size];
        int index = 0;
        for (Long element : oldArray) {
            newArray[index++] = element.longValue();
        }
        return newArray;
    }

    private float[] floats2Primitives(Collection<Float> oldArray) {
        int size = oldArray.size();
        float[] newArray = new float[size];
        int index = 0;
        for (Float element : oldArray) {
            newArray[index++] = element.floatValue();
        }
        return newArray;
    }

    private double[] doubles2Primitives(Collection<Double> oldArray) {
        int size = oldArray.size();
        double[] newArray = new double[size];
        int index = 0;
        for (Double element : oldArray) {
            newArray[index++] = element.doubleValue();
        }
        return newArray;
    }

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneIndex annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        if (data == null) {
            return indexables;
        }
        // 兼容UniMi
        type = TypeUtility.refineType(type, Collection.class);
        ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
        Type[] types = parameterizedType.getActualTypeArguments();
        Type elementType = types[0];
        Class<?> elementClazz = TypeUtility.getRawType(elementType, null);

        try {
            if (Boolean.class == elementClazz) {
                int[] array = booleans2Primitives((Collection) data);
                indexables.add(new IntPoint(path, array));
                return indexables;
            }
            if (Byte[].class == elementClazz) {
                int[] array = bytes2Primitives((Collection) data);
                indexables.add(new IntPoint(path, array));
                return indexables;
            }
            if (Short[].class == elementClazz) {
                int[] array = shorts2Primitives((Collection) data);
                indexables.add(new IntPoint(path, array));
                return indexables;
            }
            if (Integer[].class == elementClazz) {
                int[] array = integers2Primitives((Collection) data);
                indexables.add(new IntPoint(path, array));
                return indexables;
            }
            if (Long[].class == elementClazz) {
                long[] array = longs2Primitives((Collection) data);
                indexables.add(new LongPoint(path, array));
            }
            if (Float[].class == elementClazz) {
                float[] array = floats2Primitives((Collection) data);
                indexables.add(new FloatPoint(path, array));
            }
            if (Double[].class == elementClazz) {
                double[] array = doubles2Primitives((Collection) data);
                indexables.add(new DoublePoint(path, array));
            }
            // 每个字符串都索引
            if (String.class == elementClazz) {
                for (String string : (String[]) data) {
                    indexables.add(new StringField(path, string, Store.NO));
                }
                return indexables;
            }
            // TODO 此处需要代码重构
            Collection<?> collection = Collection.class.cast(data);
            Specification specification = Specification.getSpecification(elementClazz);
            IndexConverter converter = context.getIndexConverter(specification);

            // 将Collection整体索引
//            int index = 0;
            for (Object element : collection) {
                for (IndexableField indexable : converter.convert(context, path, field, annotation, elementType, element)) {
                    indexables.add(indexable);
                }
//                index++;
            }
            return indexables;
        } catch (Exception exception) {
            // TODO
            throw new StorageException(exception);
        }
    }

}
