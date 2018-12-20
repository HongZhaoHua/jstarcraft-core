package com.jstarcraft.core.distribution.identity;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.jstarcraft.core.cache.CacheObject;

@Entity
public class MockCacheObject implements CacheObject<Long> {

	@Id
	private Long id;

	private String firstName;

	private String lastName;

	private int money;

	private int token;

	public MockCacheObject() {
	}

	@Override
	public Long getId() {
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

	public static MockCacheObject instanceOf(Long id, String firstName, String lastName, int money, int token) {
		MockCacheObject instance = new MockCacheObject();
		instance.id = id;
		instance.firstName = firstName;
		instance.lastName = lastName;
		instance.money = money;
		instance.token = token;
		return instance;
	}

}
