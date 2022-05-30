package com.jstarcraft.core.storage.berkeley.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * 个人
 * 
 * @author Birdy
 *
 */
@BerkeleyConfiguration(store = "berkeley")
@Entity
public class Person implements IdentityObject<Long> {

    @PrimaryKey
    private long id;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String name;

    private Person() {
    }

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof Person))
            return false;
        Person that = (Person) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        equal.append(this.getName(), that.getName());
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(getId());
        return hash.toHashCode();
    }

}
