package com.jstarcraft.core.resource.path;

import java.io.InputStream;

public class ClassPathAdapter implements PathAdapter {

    private final String address;

    public ClassPathAdapter(String address) {
        this.address = address;
    }

    @Override
    public InputStream getStream(String path) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(address + path);
        return stream;
    }

}
