package com.jstarcraft.core.cache;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;

@Entity
@CacheConfiguration(unit = Unit.ENTITY, indexes = { "firstName", "token" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "queuePersistenceStrategy")
public class MockEntityObject implements IdentityObject<Integer> {

    @Id
    private Integer id;

    private String firstName;

    private String lastName;

    private int money;

    private int token;

    MockEntityObject() {
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
    public boolean modify(String lastName, int money, boolean result) {
        this.lastName = lastName;
        this.money = money;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MockEntityObject))
            return false;
        MockEntityObject that = (MockEntityObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.firstName, that.firstName);
        equal.append(this.lastName, that.lastName);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(firstName);
        hash.append(lastName);
        return hash.toHashCode();
    }

    public static MockEntityObject instanceOf(Integer id, String firstName, String lastName, int money, int token) {
        MockEntityObject instance = new MockEntityObject();
        instance.id = id;
        instance.firstName = firstName;
        instance.lastName = lastName;
        instance.money = money;
        instance.token = token;
        return instance;
    }

}
