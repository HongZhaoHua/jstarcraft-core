package com.jstarcraft.core.storage.mongo;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class NestObject {

    private long id;

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof NestObject))
            return false;
        NestObject that = (NestObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.id, that.id);
        equal.append(this.name, that.name);
        return equal.isEquals();
    }

    public static NestObject instanceOf(long id, String name) {
        NestObject instance = new NestObject();
        instance.id = id;
        instance.name = name;
        return instance;
    }

}
