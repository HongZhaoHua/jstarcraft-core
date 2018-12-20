package com.jstarcraft.core.storage.xlsx;

import com.jstarcraft.core.storage.annotation.StorageConfiguration;
import com.jstarcraft.core.storage.annotation.StorageId;

@StorageConfiguration
public class Biology {

	@StorageId
	private Integer id;

	private String name;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
