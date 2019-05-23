package com.jstarcraft.core.utility.reflection;

public abstract class AbstractClass {

    private String id;

    protected AbstractClass(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
