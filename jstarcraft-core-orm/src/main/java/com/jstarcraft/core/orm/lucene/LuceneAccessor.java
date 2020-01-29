package com.jstarcraft.core.orm.lucene;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmIterator;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.OrmPagination;
import com.jstarcraft.core.orm.lucene.converter.IdConverter;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.floats.FloatList;

/**
 * Lucene访问器
 * 
 * @author Birdy
 *
 */
public class LuceneAccessor implements OrmAccessor {

    /** 元数据集合 */
    private HashMap<Class<?>, LuceneMetadata> metadatas = new HashMap<>();

    private LuceneEngine engine;

    /** 编解码器映射 */
    private LuceneContext context;

    public LuceneAccessor(Collection<Class<?>> classes, LuceneEngine engine) {
        this.engine = engine;

        for (Class<?> ormClass : classes) {
            LuceneMetadata metadata = new LuceneMetadata(ormClass);
            this.metadatas.put(ormClass, metadata);
        }

        CodecDefinition definition = CodecDefinition.instanceOf(classes);
        this.context = new LuceneContext(definition);
    }

    @Override
    public Collection<? extends OrmMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T get(Class<T> clazz, K id) {
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean create(Class<T> clazz, T object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean delete(Class<T> clazz, K id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean delete(Class<T> clazz, T object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean update(Class<T> clazz, T object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, OrmCondition condition, String name, I... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, OrmCondition condition, String name, I... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> query(Class<T> clazz, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long count(Class<T> clazz) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(OrmIterator<T> iterator, Class<T> clazz, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(OrmIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(OrmIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

}
