package com.jstarcraft.core.resource.format.csv;

import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jstarcraft.core.common.conversion.csv.annotation.CsvConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.annotation.ResourceIndex;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

@ResourceConfiguration(prefix = "csv/", suffix = ".csv")
public class CsvPerson {

    public static final String INDEX_NAME = "name";
    public static final String INDEX_AGE = "age";

    @ResourceId
    @ExcelProperty("id")
    private Integer id;

    @ResourceIndex(name = INDEX_NAME, unique = true)
    @ExcelProperty("name")
    private String name;

    @ResourceIndex(name = INDEX_AGE, unique = false)
    @ExcelProperty("age")
    private int age;

    @ExcelProperty("sex")
    private boolean sex;

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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

}
