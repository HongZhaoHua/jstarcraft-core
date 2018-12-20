package com.jstarcraft.core.cache.aspect;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.cache.CacheObject;

/**
 * 模仿缓存对象
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class MockCacheObject<T extends Comparable<T> & Serializable> implements CacheObject<T> {

	private T id;

	private int hash;

	public MockCacheObject(T id) {
		this.id = id;
		HashCodeBuilder hash = new HashCodeBuilder();
		hash.append(this.id);
		this.hash = hash.toHashCode();
	}

	@Override
	public T getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return hash;

	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (!(object instanceof MockCacheObject))
			return false;
		MockCacheObject that = (MockCacheObject) object;
		EqualsBuilder equal = new EqualsBuilder();
		equal.append(this.getId(), that.getId());
		return equal.isEquals();
	}

}
