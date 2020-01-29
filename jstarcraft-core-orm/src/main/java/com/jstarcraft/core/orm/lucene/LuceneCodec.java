package com.jstarcraft.core.orm.lucene;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.orm.exception.OrmException;
import com.jstarcraft.core.orm.lucene.annotation.LuceneId;
import com.jstarcraft.core.orm.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.orm.lucene.annotation.LuceneSort;
import com.jstarcraft.core.orm.lucene.annotation.LuceneStore;
import com.jstarcraft.core.orm.lucene.converter.IdConverter;
import com.jstarcraft.core.orm.lucene.converter.IndexConverter;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.converter.SortConverter;
import com.jstarcraft.core.orm.lucene.converter.StoreConverter;
import com.jstarcraft.core.utility.KeyValue;

/**
 * 搜索编解码器
 * 
 * @author Birdy
 *
 * @param <T>
 */
// TODO 以后会整合到Searcher
public class LuceneCodec<S, L> {

    static final String ID = "_id";

    static final String VERSION = "_version";

    private LuceneContext context;

    private ClassDefinition saveDefinition;

    private ClassDefinition loadDefinition;

    public LuceneCodec(Class<S> saveClass, Class<L> loadClass) {
        CodecDefinition saveDefinition = CodecDefinition.instanceOf(saveClass);
        CodecDefinition loadDefinition = CodecDefinition.instanceOf(loadClass);

        this.context = new LuceneContext(saveDefinition, loadDefinition);

        this.saveDefinition = saveDefinition.getClassDefinition(saveClass);
        this.loadDefinition = loadDefinition.getClassDefinition(loadClass);
    }

    /**
     * 解码
     * 
     * @param document
     * @return
     */
    public L decode(Document document) {
        try {
            NavigableMap<String, IndexableField> indexables = new TreeMap<>();
            for (IndexableField indexable : document) {
                indexables.put(indexable.name(), indexable);
            }
            L instance = (L) loadDefinition.getInstance();
            {
                KeyValue<Field, IdConverter> keyValue = this.context.getIdKeyValue(this.saveDefinition.getType());
                if (keyValue != null) {
                    Field field = keyValue.getKey();
                    IdConverter converter = keyValue.getValue();
                    LuceneId annotation = field.getAnnotation(LuceneId.class);
                    Type type = field.getGenericType();
                    IndexableField indexable = indexables.get(ID);
                    Object data = converter.decode(type, indexable.stringValue());
                    field.set(instance, data);
                }
            }
            for (KeyValue<Field, StoreConverter> keyValue : this.context.getStoreKeyValues(this.loadDefinition.getType())) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                StoreConverter converter = keyValue.getValue();
                LuceneStore annotation = field.getAnnotation(LuceneStore.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = converter.decode(this.context, path, field, annotation, type, indexables);
                field.set(instance, data);
            }
            return instance;
        } catch (Exception exception) {
            // TODO
            throw new OrmException(exception);
        }
    }

    /**
     * 编码
     * 
     * @param object
     * @return
     */
    public Document encode(S object) {
        try {
            Document document = new Document();
            {
                KeyValue<Field, IdConverter> keyValue = this.context.getIdKeyValue(this.saveDefinition.getType());
                if (keyValue != null) {
                    Field field = keyValue.getKey();
                    IdConverter converter = keyValue.getValue();
                    LuceneId annotation = field.getAnnotation(LuceneId.class);
                    Type type = field.getGenericType();
                    Object data = field.get(object);
                    String id = converter.encode(type, data);
                    IndexableField indexable = null;
                    indexable = new StringField(ID, id, Store.YES);
                    document.add(indexable);
                    indexable = new BinaryDocValuesField(ID, new BytesRef(id));
                    document.add(indexable);
                    long version = System.currentTimeMillis();
                    indexable = new NumericDocValuesField(VERSION, version);
                    document.add(indexable);
                }
            }
            for (KeyValue<Field, IndexConverter> keyValue : this.context.getIndexKeyValues(this.saveDefinition.getType())) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                IndexConverter converter = keyValue.getValue();
                LuceneIndex annotation = field.getAnnotation(LuceneIndex.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = field.get(object);
                for (IndexableField indexable : converter.convert(this.context, path, field, annotation, type, data)) {
                    document.add(indexable);
                }
            }
            for (KeyValue<Field, SortConverter> keyValue : this.context.getSortKeyValues(this.saveDefinition.getType())) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                SortConverter converter = keyValue.getValue();
                LuceneSort annotation = field.getAnnotation(LuceneSort.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = field.get(object);
                for (IndexableField indexable : converter.convert(this.context, path, field, annotation, type, data)) {
                    document.add(indexable);
                }
            }
            for (KeyValue<Field, StoreConverter> keyValue : this.context.getStoreKeyValues(this.saveDefinition.getType())) {
                // TODO 此处代码可以优反射次数.
                Field field = keyValue.getKey();
                StoreConverter converter = keyValue.getValue();
                LuceneStore annotation = field.getAnnotation(LuceneStore.class);
                String path = field.getName();
                Type type = field.getGenericType();
                Object data = field.get(object);
                for (IndexableField indexable : converter.encode(this.context, path, field, annotation, type, data).values()) {
                    document.add(indexable);
                }
            }
            return document;
        } catch (Exception exception) {
            // TODO
            throw new OrmException(exception);
        }
    }

}
