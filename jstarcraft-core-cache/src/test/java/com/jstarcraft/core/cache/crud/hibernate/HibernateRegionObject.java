package com.jstarcraft.core.cache.crud.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;

@Entity
@CacheConfiguration(unit = Unit.REGION, indexes = { "owner" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "promptPersistenceStrategy")
public class HibernateRegionObject implements IdentityObject<Integer> {

    @Id
    private Integer id;

    private int owner;

    HibernateRegionObject() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    @CacheChange(values = { "true" })
    public boolean modify(int owner, boolean modify) {
        this.owner = owner;
        return modify;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof HibernateRegionObject))
            return false;
        HibernateRegionObject that = (HibernateRegionObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(getId());
        return hash.toHashCode();
    }

    public static HibernateRegionObject instanceOf(Integer id, int owner) {
        HibernateRegionObject instance = new HibernateRegionObject();
        instance.id = id;
        instance.owner = owner;
        return instance;
    }

}
