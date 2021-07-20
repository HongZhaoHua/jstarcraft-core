package com.jstarcraft.core.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.RawLocalFileSystem;

import com.jstarcraft.core.io.hdfs.HdfsStreamManager;

public class HdfsStreamManagerTestCase extends StreamManagerTestCase {

    @Override
    protected StreamManager getStreamManager() {
        Configuration configuration = new Configuration();
        try {
            FileSystem system = RawLocalFileSystem.get(configuration);
            return new HdfsStreamManager(system, "hdfs");
        } catch (Exception exception) {
            return null;
        }
    }

}
