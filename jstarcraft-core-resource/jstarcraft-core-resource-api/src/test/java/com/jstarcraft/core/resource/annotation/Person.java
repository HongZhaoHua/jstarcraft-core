package com.jstarcraft.core.resource.annotation;

@ResourceConfiguration(suffix = ".xlsx")
public class Person {

    public static final String INDEX_NAME = "name";

    @ResourceId
    private Integer id;

    @ResourceIndex(name = INDEX_NAME, unique = true)
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
