package com.jstarcraft.core.cache.crud.mongo;



import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;

@Document
@CacheConfiguration(unit = Unit.ENTITY, indexes = { "firstName", "token" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "queuePersistenceStrategy")
public class MongoEntityObject implements IdentityObject<Integer> {

	@Id
	private Integer id;

	@Indexed
	private String firstName;

	@Indexed
	private String lastName;

	private int money;

	private int token;

	MongoEntityObject() {
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
		if (!(object instanceof MongoEntityObject))
			return false;
		MongoEntityObject that = (MongoEntityObject) object;
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

	public static MongoEntityObject instanceOf(Integer id, String firstName, String lastName, int money, int token) {
		MongoEntityObject instance = new MongoEntityObject();
		instance.id = id;
		instance.firstName = firstName;
		instance.lastName = lastName;
		instance.money = money;
		instance.token = token;
		return instance;
	}

}
