package com.jstarcraft.core.resource.format.excel;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;

@ResourceConfiguration(prefix = "excel/", suffix = ".xlsx")
public class XlsxBiology {

    @ResourceId
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
