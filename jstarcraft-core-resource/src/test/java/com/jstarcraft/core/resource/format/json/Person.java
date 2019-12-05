package com.jstarcraft.core.resource.format.json;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.annotation.ResourceIndex;
import com.jstarcraft.core.utility.StringUtility;

@ResourceConfiguration(prefix = "json/", suffix = ".js")
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

    public String getDescription() {
        return StringUtility.reflect(this);
    }

}
