package com.jstarcraft.core.resource.json;

import com.jstarcraft.core.resource.ResourceStorage;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.annotation.ResourceIndex;
import com.jstarcraft.core.resource.annotation.ResourceReference;
import com.jstarcraft.core.utility.StringUtility;

@ResourceConfiguration
public class Person {

	public static final String INDEX_NAME = "name";
	public static final String INDEX_AGE = "age";

	@ResourceId
	private Integer id;

	@ResourceIndex(name = INDEX_NAME, unique = true)
	private String name;

	@ResourceIndex(name = INDEX_AGE, unique = false)
	private int age;

	private boolean sex;

	private Integer childId;

	@ResourceReference(expression = "instance.getChildId()")
	private transient Person child;

	@ResourceReference
	private transient MockSpringObject reference;

	@ResourceReference
	private transient ResourceStorage<Integer, Person> storage;

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

	public ResourceStorage<Integer, Person> getStorage() {
		return storage;
	}

	public String getDescription() {
		return StringUtility.reflect(this);
	}

}
