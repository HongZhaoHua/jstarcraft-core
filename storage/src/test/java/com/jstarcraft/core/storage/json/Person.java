package com.jstarcraft.core.storage.json;

import com.jstarcraft.core.storage.Storage;
import com.jstarcraft.core.storage.annotation.StorageConfiguration;
import com.jstarcraft.core.storage.annotation.StorageId;
import com.jstarcraft.core.storage.annotation.StorageIndex;
import com.jstarcraft.core.storage.annotation.StorageReference;
import com.jstarcraft.core.utility.StringUtility;

@StorageConfiguration
public class Person {

	public static final String INDEX_NAME = "name";
	public static final String INDEX_AGE = "age";

	@StorageId
	private Integer id;

	@StorageIndex(name = INDEX_NAME, unique = true)
	private String name;

	@StorageIndex(name = INDEX_AGE, unique = false)
	private int age;

	private boolean sex;

	private Integer childId;

	@StorageReference(expression = "instance.getChildId()")
	private transient Person child;

	@StorageReference
	private transient MockSpringObject reference;

	@StorageReference
	private transient Storage<Integer, Person> storage;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public boolean isSex() {
		return sex;
	}

	public Integer getChildId() {
		return childId;
	}

	public Person getChild() {
		return child;
	}

	public MockSpringObject getReference() {
		return reference;
	}

	public Storage<Integer, Person> getStorage() {
		return storage;
	}

	public String getDescription() {
		return StringUtility.reflect(this);
	}

}
