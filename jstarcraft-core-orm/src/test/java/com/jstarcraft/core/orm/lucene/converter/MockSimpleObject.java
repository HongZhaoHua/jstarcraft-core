package com.jstarcraft.core.orm.lucene.converter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.jstarcraft.core.orm.lucene.annotation.SearchIndex;
import com.jstarcraft.core.orm.lucene.annotation.SearchSort;
import com.jstarcraft.core.orm.lucene.annotation.SearchStore;

/**
 * 模仿简单对象
 * 
 * @author Birdy
 *
 */
public class MockSimpleObject {

    @SearchIndex
    @SearchSort
    @SearchStore
    private long id;

    @SearchIndex
    @SearchSort
    @SearchStore
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MockSimpleObject that = (MockSimpleObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.id, that.id);
        equal.append(this.name, that.name);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(id);
        hash.append(name);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder string = new ToStringBuilder(this);
        string.append(id);
        string.append(name);
        return string.toString();
    }

    public static MockSimpleObject instanceOf(long id, String name) {
        MockSimpleObject instance = new MockSimpleObject();
        instance.id = id;
        instance.name = name;
        return instance;
    }

}