package com.jstarcraft.core.cache.crud.mongo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.utility.IdentityObject;

@Document
@CacheConfiguration(unit = Unit.REGION, indexes = { "owner" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "queuePersistenceStrategy")
public class MongoRegionObject implements IdentityObject<Integer> {

    @Id
    private Integer id;

    @Indexed
    private int owner;

    MongoRegionObject() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    @CacheChange(values = { "true" })
    public boolean modify(int owner, boolean result) {
        this.owner = owner;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MongoRegionObject))
            return false;
        MongoRegionObject that = (MongoRegionObject) object;
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

    public static MongoRegionObject instanceOf(Integer id, int owner) {
        MongoRegionObject instance = new MongoRegionObject();
        instance.id = id;
        instance.owner = owner;
        return instance;
    }

}
