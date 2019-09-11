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
@CacheConfiguration(unit = Unit.ENTITY, indexes = { "firstName", "token" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "promptPersistenceStrategy")
public class HibernateEntityObject implements IdentityObject<Integer> {

    @Id
    private Integer id;

    private String firstName;

    private String lastName;

    private int money;

    private int token;

    HibernateEntityObject() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMoney() {
        return money;
    }

    public int getToken() {
        return token;
    }

    public String[] toNames() {
        return new String[] { firstName, lastName };
    }

    public int[] toCurrencies() {
        return new int[] { money, token };
    }

    @CacheChange(values = { "true" })
    public boolean modify(String lastName, int money, boolean modify) {
        this.lastName = lastName;
        this.money = money;
        return modify;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof HibernateEntityObject))
            return false;
        HibernateEntityObject that = (HibernateEntityObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getFirstName(), that.getFirstName());
        equal.append(this.getLastName(), that.getLastName());
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(getFirstName());
        hash.append(getLastName());
        return hash.toHashCode();
    }

    public static HibernateEntityObject instanceOf(Integer id, String firstName, String lastName, int money, int token) {
        HibernateEntityObject instance = new HibernateEntityObject();
        instance.id = id;
        instance.firstName = firstName;
        instance.lastName = lastName;
        instance.money = money;
        instance.token = token;
        return instance;
    }

}
