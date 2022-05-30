package com.jstarcraft.core.storage.lucene;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.annotation.LuceneSort;
import com.jstarcraft.core.storage.lucene.annotation.LuceneStore;

/**
 * 模仿简单对象
 * 
 * @author Birdy
 *
 */
public class MockSimpleObject {

    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private long id;

    @LuceneIndex
    @LuceneSort
    @LuceneStore
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
