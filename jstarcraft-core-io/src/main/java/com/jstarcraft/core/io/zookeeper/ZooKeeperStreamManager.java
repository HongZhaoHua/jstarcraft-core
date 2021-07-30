package com.jstarcraft.core.io.zookeeper;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import com.jstarcraft.core.io.StreamManager;

public class ZooKeeperStreamManager implements StreamManager {

    private CuratorFramework curator;

    private String directory;

    public ZooKeeperStreamManager(CuratorFramework curator, String directory) {
        this.directory = directory;
    }

    @Override
    public void saveResource(String path, InputStream stream) {
        // TODO Auto-generated method stub

    }

    @Override
    public void waiveResource(String path) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean haveResource(String path) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public InputStream retrieveResource(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<String> iterateResources(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getUpdatedAt(String path) {
        try {
            Stat state = curator.checkExists().forPath(directory + path);
            return state.getMtime();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
