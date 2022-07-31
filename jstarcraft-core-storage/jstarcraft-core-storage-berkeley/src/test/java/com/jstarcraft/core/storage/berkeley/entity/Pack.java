package com.jstarcraft.core.storage.berkeley.entity;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.jstarcraft.core.storage.berkeley.persistent.Item;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * 包裹
 * 
 * @author Administrator
 *
 */
@BerkeleyConfiguration(store = "berkeley", version = "version")
@Entity
public class Pack implements IdentityObject<Long> {

    @PrimaryKey
    private long id;

    /** 条目 */
    private ArrayList<Item> items;

    /** 版本 */
    protected int version;

    /** 个人标识 */
    @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Person.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
    private Long personId;

    /** 大小 */
    private int size;

    private Pack() {
    }

    public Pack(long id, int size, Long personId) {
        this.id = id;
        this.items = new ArrayList<>(size);
        this.personId = personId;
        this.size = size;
    }

    @Override
    public Long getId() {
        return id;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Long getPersonId() {
        return personId;
    }

    public int getSize() {
        return size;
    }

    public int getVersion() {
        return version;
    }

    public void push(Item item) {
        items.add(item);
    }

    public Item pull() {
        return items.remove(0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof Pack))
            return false;
        Pack that = (Pack) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        equal.append(this.getItems(), that.getItems());
        equal.append(this.getPersonId(), that.getPersonId());
        equal.append(this.getSize(), that.getSize());
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(getId());
        return hash.toHashCode();
    }

}
