package com.jstarcraft.core.distribution.database;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.jstarcraft.core.cache.CacheObject;

@Entity
public class MockObject implements CacheObject<String> {

	@Id
	private String id;

	public MockObject() {
	}

	@Override
	public String getId() {
		return id;
	}

	public static MockObject instanceOf(String id) {
		MockObject instance = new MockObject();
		instance.id = id;
		return instance;
	}

}
