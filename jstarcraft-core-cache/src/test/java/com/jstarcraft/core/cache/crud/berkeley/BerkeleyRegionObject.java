package com.jstarcraft.core.cache.crud.berkeley;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@BerkeleyConfiguration(store = "berkeley")
@Entity
@CacheConfiguration(unit = Unit.REGION, indexes = { "owner" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "promptPersistenceStrategy")
public class BerkeleyRegionObject implements IdentityObject<Integer> {

    @PrimaryKey
    private Integer id;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private int owner;

    BerkeleyRegionObject() {
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
        if (!(object instanceof BerkeleyRegionObject))
            return false;
        BerkeleyRegionObject that = (BerkeleyRegionObject) object;
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

    public static BerkeleyRegionObject instanceOf(Integer id, int owner) {
        BerkeleyRegionObject instance = new BerkeleyRegionObject();
        instance.id = id;
        instance.owner = owner;
        return instance;
    }

}
