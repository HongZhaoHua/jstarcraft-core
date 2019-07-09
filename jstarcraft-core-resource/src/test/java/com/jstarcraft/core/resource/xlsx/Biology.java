package com.jstarcraft.core.resource.xlsx;

import com.jstarcraft.core.resource.annotation.StorageConfiguration;
import com.jstarcraft.core.resource.annotation.StorageId;

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
