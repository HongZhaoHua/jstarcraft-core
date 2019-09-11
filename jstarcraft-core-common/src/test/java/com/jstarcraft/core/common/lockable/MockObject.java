package com.jstarcraft.core.common.lockable;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 模仿对象
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class MockObject<T extends Comparable<T> & Serializable> implements IdentityObject<T> {

    private T id;

    private int hash;

    public MockObject(T id) {
        this.id = id;
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(this.id);
        this.hash = hash.toHashCode();
    }

    @Override
    public T getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return hash;

    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MockObject))
            return false;
        MockObject that = (MockObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        return equal.isEquals();
    }

}
