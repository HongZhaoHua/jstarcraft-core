package com.jstarcraft.core.resource.csv;

import java.util.ArrayList;
import java.util.HashMap;

import com.jstarcraft.core.common.conversion.csv.annotation.CsvConfiguration;
import com.jstarcraft.core.resource.ResourceManager;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.annotation.ResourceIndex;
import com.jstarcraft.core.resource.annotation.ResourceReference;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

@ResourceConfiguration
@CsvConfiguration({ "id", "name", "age", "sex", "object", "array", "map", "list", "childId" })
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

    @ResourceReference(expression = "instance.getChildId()")
    private transient Person child;

    @ResourceReference
    private transient MockSpringObject reference;

    @ResourceReference
    private transient ResourceManager<Integer, Person> storage;

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

    public Person getChild() {
        return child;
    }

    public MockSpringObject getReference() {
        return reference;
    }

    public ResourceManager<Integer, Person> getStorage() {
        return storage;
    }

    public String getDescription() {
        return StringUtility.reflect(this);
    }

}
