package com.jstarcraft.core.io;

import com.jstarcraft.core.io.disk.DiskStreamManager;

public class DiskStreamManagerTestCase extends StreamManagerTestCase {

    @Override
    protected StreamManager getStreamManager() {
        return new DiskStreamManager("disk");
    }

}
