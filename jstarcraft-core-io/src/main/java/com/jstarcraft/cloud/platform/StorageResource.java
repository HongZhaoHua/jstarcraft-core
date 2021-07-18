package com.jstarcraft.cloud.platform;

import java.io.InputStream;

/**
 * 仓库资源
 * 
 * @author Birdy
 *
 */
public class StorageResource {

    private StorageMetadata metadata;

    private InputStream stream;

    public StorageResource(StorageMetadata metadata, InputStream stream) {
        this.metadata = metadata;
        this.stream = stream;
    }

    public StorageMetadata getMetadata() {
        return metadata;
    }

    public InputStream getStream() {
        return stream;
    }

}
