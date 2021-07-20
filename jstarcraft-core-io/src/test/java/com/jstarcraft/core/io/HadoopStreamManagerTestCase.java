package com.jstarcraft.core.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.RawLocalFileSystem;

import com.jstarcraft.core.io.hdfs.HadoopStreamManager;

public class HadoopStreamManagerTestCase extends StreamManagerTestCase {

    @Override
    protected StreamManager getStreamManager() {
        Configuration configuration = new Configuration();
        try {
            FileSystem system = RawLocalFileSystem.get(configuration);
            return new HadoopStreamManager(system, "hdfs");
        } catch (Exception exception) {
            return null;
        }
    }

}
