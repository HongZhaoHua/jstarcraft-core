package com.jstarcraft.core.resource.format.yaml;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.annotation.ResourceIndex;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

@ResourceConfiguration(prefix = "yaml/", suffix = ".yml")
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

    private KeyValue<Integer, String> object;

    private KeyValue<Integer, String>[] array;

    private HashMap<String, KeyValue<Integer, String>> map;

    private ArrayList<KeyValue<Integer, String>> list;

    private Integer childId;

    Person() {
    }

    public Person(Integer id, String name, int age, boolean sex, KeyValue<Integer, String> object, KeyValue<Integer, String>[] array, HashMap<String, KeyValue<Integer, String>> map, ArrayList<KeyValue<Integer, String>> list) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.object = object;
        this.array = array;
        this.map = map;
        this.list = list;
    }

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

    public KeyValue<Integer, String> getObject() {
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

    @JsonIgnore
    public String getDescription() {
        return StringUtility.reflect(this);
    }

}
