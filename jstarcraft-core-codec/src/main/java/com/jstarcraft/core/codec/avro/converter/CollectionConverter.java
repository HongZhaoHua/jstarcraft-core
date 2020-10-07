package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 集合转换器
 * 
 * @author Yue Zhen Wei
 *
 */
public class CollectionConverter extends AvroConverter<Collection<Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionConverter.class);

    @Override
    protected Collection<Object> readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        Class<?> clazz = TypeUtility.getRawType(type, null);
        Collection<Object> resultCollection = (Collection<Object>) clazz.newInstance();
        Collection<Object> inputCollection = (Collection<Object>) input;

        Type[] types = super.getTypes(type, Collection.class);
        AvroConverter avroConverter = avroReader.getAvroConverter(Specification.getSpecification(types[0]));
        for (Object value : inputCollection) {
            resultCollection.add(avroConverter.readValue(avroReader, value, types[0]));
        }
        return resultCollection;
    }

    @Override
    protected Object writeValue(AvroWriter writer, Collection<Object> value, Type type) throws Exception {
        Type refineType = TypeUtility.refineType(type, Collection.class);
        ParameterizedType cast = ParameterizedType.class.cast(refineType);
        Type actualTypeArgument = cast.getActualTypeArguments()[0];
        AvroConverter avroConverter = writer.getAvroConverter(Specification.getSpecification(actualTypeArgument));
        return value.stream().map(index -> {
            Object converterValue = null;
            try {
                converterValue = avroConverter.writeValue(writer, index, actualTypeArgument);
            } catch (Exception exception) {
                LOGGER.error("Avro 类型转换错误  cause: {} \n error: {}", exception.getCause(), exception.getMessage());
            }
            return converterValue;
        }).collect(Collectors.toList());

    }
}
