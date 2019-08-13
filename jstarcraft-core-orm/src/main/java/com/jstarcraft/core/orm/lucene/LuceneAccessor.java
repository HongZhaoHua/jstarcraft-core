package com.jstarcraft.core.orm.lucene;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmIterator;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.OrmPagination;

/**
 * Lucene访问器
 * 
 * @author Birdy
 *
 */
public class LuceneAccessor implements OrmAccessor {

    @Override
    public Collection<? extends OrmMetadata> getAllMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T get(Class<T> objectType, K id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K create(Class<T> objectType, T object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void delete(Class<T> objectType, K id) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void delete(Class<T> objectType, T object) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void update(Class<T> objectType, T object) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> objectType, K from, K to) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> objectType, K from, K to) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> objectType, OrmCondition condition, String name, I... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> objectType, OrmCondition condition, String name, I... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> query(Class<T> objectType, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long count(Class<T> objectType) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> objectType, Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> objectType, Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(OrmIterator<T> iterator, Class<T> objectType, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(OrmIterator<T> iterator, Class<T> objectType, Map<String, Object> condition, OrmPagination pagination) {
        // TODO Auto-generated method stub

    }

}
