package com.jstarcraft.core.cache.transience;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 内存元素
 * 
 * @author Birdy
 *
 */
public class TransienceElement {

    /** 缓存主键 */
    private final Comparable cacheId;
    /** 缓存对象 */
    private final IdentityObject<?> cacheObject;

    public TransienceElement(IdentityObject<?> cacheObject) {
        this.cacheId = cacheObject.getId();
        this.cacheObject = cacheObject;
    }

    public Comparable getCacheId() {
        return cacheId;
    }

    public IdentityObject<?> getCacheObject() {
        return cacheObject;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        TransienceElement that = (TransienceElement) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.cacheId, that.cacheId);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        return cacheId.hashCode();
    }

}
