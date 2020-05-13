package com.jstarcraft.core.storage;

import java.util.Arrays;
import java.util.Objects;

import com.jstarcraft.core.storage.exception.StorageQueryException;

public class StorageCondition<V> {

	private ConditionType type;

	private V[] values;

	public StorageCondition(ConditionType type, V... values) {
		if (!type.check(values)) {
			throw new StorageQueryException();
		}
		this.type = type;
		this.values = values;
	}

	public ConditionType getType() {
		return type;
	}

	public V[] getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = prime * hash + Objects.hash(type);
		hash = prime * hash + Arrays.deepHashCode(values);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		StorageCondition taht = (StorageCondition) object;
		return this.type == taht.type && Arrays.deepEquals(this.values, taht.values);
	}

}
