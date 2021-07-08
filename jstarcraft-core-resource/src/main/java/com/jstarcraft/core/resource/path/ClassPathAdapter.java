package com.jstarcraft.core.resource.path;

import java.io.InputStream;

public class ClassPathAdapter implements PathAdapter {

    private final ClassLoader loader;

    private final String address;

    public ClassPathAdapter(String address) {
        this(Thread.currentThread().getContextClassLoader(), address);
    }

    public ClassPathAdapter(ClassLoader loader, String address) {
        this.loader = loader;
        this.address = address;
    }

    @Override
    public InputStream getStream(String path) {
        InputStream stream = loader.getResourceAsStream(address + path);
        return stream;
    }

}
