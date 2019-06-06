package com.jstarcraft.core.cache.crud.mybatis;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;

@Entity
//@TableName("MyBatisRegionObject")
@CacheConfiguration(unit = Unit.REGION, indexes = { "owner" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "promptPersistenceStrategy")
public class MyBatisRegionObject implements IdentityObject<Integer> {

    @Id
    @TableId
    private Integer id;

    private int owner;

    MyBatisRegionObject() {
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
        if (!(object instanceof MyBatisRegionObject))
            return false;
        MyBatisRegionObject that = (MyBatisRegionObject) object;
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

    public static MyBatisRegionObject instanceOf(Integer id, int owner) {
        MyBatisRegionObject instance = new MyBatisRegionObject();
        instance.id = id;
        instance.owner = owner;
        return instance;
    }

}
