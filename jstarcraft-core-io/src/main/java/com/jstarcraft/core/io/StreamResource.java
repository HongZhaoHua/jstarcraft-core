package com.jstarcraft.core.io;

import java.io.InputStream;

/**
 * 仓库资源
 * 
 * @author Birdy
 *
 */
public class StreamResource {

    private StreamMetadata metadata;

    private InputStream stream;

    public StreamResource(StreamMetadata metadata, InputStream stream) {
        this.metadata = metadata;
        this.stream = stream;
    }

    public StreamMetadata getMetadata() {
        return metadata;
    }

    public InputStream getStream() {
        return stream;
    }

}
