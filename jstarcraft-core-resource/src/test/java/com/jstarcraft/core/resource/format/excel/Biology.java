package com.jstarcraft.core.resource.format.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;

@ResourceConfiguration(prefix = "excel/", suffix = ".xlsx")
public class Biology {

    @ResourceId
    @ExcelProperty("id")
    private Integer id;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("fomula")
    private String fomula;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFomula() {
        return fomula;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFomula(String fomula) {
        this.fomula = fomula;
    }

}
