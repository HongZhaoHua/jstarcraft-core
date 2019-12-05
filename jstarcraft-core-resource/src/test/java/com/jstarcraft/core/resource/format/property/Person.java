package com.jstarcraft.core.resource.format.property;

import java.util.ArrayList;
import java.util.HashMap;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.annotation.ResourceIndex;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

@ResourceConfiguration(prefix = "property/", suffix = ".properties")
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

    private KeyValue<String, String> object;

    private KeyValue<Integer, String>[] array;

    private HashMap<String, KeyValue<Integer, String>> map;

    private ArrayList<KeyValue<Integer, String>> list;

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

    public KeyValue<String, String> getObject() {
        return object;
    }

    public KeyValue<Integer, String>[] getArray() {
        return array;
    }

    public HashMap<String, KeyValue<Integer, String>> getMap() {
        return map;
    }

    public ArrayList<KeyValue<Integer, String>> getList() {
        return list;
    }

    public Integer getChildId() {
        return childId;
    }

    public String getDescription() {
        return StringUtility.reflect(this);
    }

}
