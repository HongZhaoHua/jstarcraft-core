package com.jstarcraft.core.resource.path;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.curator.framework.CuratorFramework;

public class ZooKeeperPathAdapter implements PathAdapter {

    private CuratorFramework curator;

    private String directory;

    public ZooKeeperPathAdapter(CuratorFramework curator, String directory) {
        this.curator = curator;
        this.directory = directory;
    }

    @Override
    public InputStream getStream(String path) throws Exception {
        try {
            byte[] data = curator.getData().forPath(directory + path);
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            return stream;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
