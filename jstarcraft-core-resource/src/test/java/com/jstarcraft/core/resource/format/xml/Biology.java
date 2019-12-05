package com.jstarcraft.core.resource.format.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;

@ResourceConfiguration(prefix = "xml/", suffix = ".xml")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Biology {

    @ResourceId
    @XmlAttribute
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
