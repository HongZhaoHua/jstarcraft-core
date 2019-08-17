package com.jstarcraft.core.orm.lucene.converter.index;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.orm.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.orm.lucene.annotation.LuceneTerm;
import com.jstarcraft.core.orm.lucene.converter.IndexConverter;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;

/**
 * 字符串索引转换器
 * 
 * @author Birdy
 *
 */
public class StringIndexConverter implements IndexConverter {

    @Override
    public Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneIndex annotation, Type type, Object data) {
        Collection<IndexableField> indexables = new LinkedList<>();
        FieldType configuration = new FieldType();
        configuration.setIndexOptions(IndexOptions.DOCS);
        if (annotation.analyze()) {
            configuration.setTokenized(true);

            LuceneTerm negative = annotation.negative();
            if (negative.offset()) {
                configuration.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            } else if (negative.position()) {
                configuration.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            } else if (negative.frequency()) {
                configuration.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            }

            LuceneTerm positive = annotation.positive();
            if (positive.offset()) {
                configuration.setStoreTermVectorOffsets(true);
            }
            if (positive.position()) {
                configuration.setStoreTermVectorPositions(true);
            }
            if (positive.frequency()) {
                configuration.setStoreTermVectors(true);
            }
        }
        indexables.add(new org.apache.lucene.document.Field(path, (String) data, configuration));
        return indexables;
    }

}
