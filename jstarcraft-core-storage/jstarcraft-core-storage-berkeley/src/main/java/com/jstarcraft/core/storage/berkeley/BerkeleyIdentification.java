package com.jstarcraft.core.storage.berkeley;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Berkeley标识
 * 
 * @author Birdy
 *
 */
class BerkeleyIdentification {

    private final Class<?> clazz;

    private final Object id;

    private final int hash;

    BerkeleyIdentification(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(clazz);
        hash.append(id);
        this.hash = hash.toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        BerkeleyIdentification that = (BerkeleyIdentification) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.clazz, that.clazz);
        equal.append(this.id, that.id);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        return hash;
    }

}
