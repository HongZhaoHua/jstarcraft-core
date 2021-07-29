package com.jstarcraft.core.resource.path;

import java.io.InputStream;

import com.jstarcraft.core.io.StreamManager;

public class IoPathAdapter implements PathAdapter {

    private StreamManager manager;

    private String directory;

    public IoPathAdapter(StreamManager manager, String directory) {
        this.manager = manager;
        this.directory = directory;
    }

    @Override
    public InputStream getStream(String path) throws Exception {
        InputStream stream = manager.retrieveResource(directory + path);
        return stream;
    }

}
