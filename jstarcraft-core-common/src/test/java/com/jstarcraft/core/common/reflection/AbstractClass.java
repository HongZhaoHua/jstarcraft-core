package com.jstarcraft.core.common.reflection;

public abstract class AbstractClass {

    private String id;

    protected AbstractClass(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
