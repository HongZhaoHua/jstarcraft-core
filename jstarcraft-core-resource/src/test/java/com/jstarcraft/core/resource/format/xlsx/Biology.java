package com.jstarcraft.core.resource.format.xlsx;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;

@ResourceConfiguration(prefix = "xlsx/", suffix = ".xlsx")
public class Biology {

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
