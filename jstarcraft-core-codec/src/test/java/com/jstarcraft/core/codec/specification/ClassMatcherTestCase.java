package com.jstarcraft.core.codec.specification;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.MockEnumeration;

public class ClassMatcherTestCase {

    @Test
    public void testMatch() {
        ClassMatcher<Class<?>> matcher = new ClassMatcher<>();
        Assert.assertNull(matcher.match(MockEnumeration.class));
        matcher.set(Enum.class, Enum.class);
        Assert.assertEquals(Enum.class, matcher.match(MockEnumeration.class));
        matcher.set(MockEnumeration.class, MockEnumeration.class);
        Assert.assertEquals(MockEnumeration.class, matcher.match(MockEnumeration.class));
        matcher.set(MockEnumeration.class, null);
        Assert.assertEquals(Enum.class, matcher.match(MockEnumeration.class));
        matcher.set(Enum.class, null);
        Assert.assertNull(matcher.match(MockEnumeration.class));

        matcher.set(Type.class, Type.class);
        Assert.assertEquals(Type.class, matcher.match(Type.class));
        Assert.assertEquals(Type.class, matcher.match(Class.class));
        Assert.assertEquals(Type.class, matcher.match(GenericArrayType.class));
        Assert.assertEquals(Type.class, matcher.match(ParameterizedType.class));
        Assert.assertEquals(Type.class, matcher.match(TypeVariable.class));
        Assert.assertEquals(Type.class, matcher.match(WildcardType.class));
        matcher.set(Type.class, null);
        Assert.assertNull(matcher.match(Type.class));
        Assert.assertNull(matcher.match(Class.class));
        Assert.assertNull(matcher.match(GenericArrayType.class));
        Assert.assertNull(matcher.match(ParameterizedType.class));
        Assert.assertNull(matcher.match(TypeVariable.class));
        Assert.assertNull(matcher.match(WildcardType.class));

        matcher.set(Collection.class, Collection.class);
        Assert.assertEquals(Collection.class, matcher.match(AbstractCollection.class));
        Assert.assertEquals(Collection.class, matcher.match(AbstractList.class));
        Assert.assertEquals(Collection.class, matcher.match(AbstractSet.class));
        matcher.set(Map.class, Map.class);
        Assert.assertEquals(Map.class, matcher.match(new AbstractMap() {

            @Override
            public Set entrySet() {
                return null;
            }

        }.getClass()));

        matcher.set(Object[].class, Object[].class);
        Assert.assertEquals(Object[].class, matcher.match(Object[].class));
        Assert.assertEquals(Object[].class, matcher.match(Object[][].class));
        Assert.assertEquals(Object[].class, matcher.match(Comparable[].class));
        Assert.assertEquals(Object[].class, matcher.match(Comparable[][].class));
        matcher.set(Comparable[].class, Comparable[].class);
        Assert.assertEquals(Comparable[].class, matcher.match(Comparable[].class));
        Assert.assertEquals(Object[].class, matcher.match(Comparable[][].class));
    }

}
