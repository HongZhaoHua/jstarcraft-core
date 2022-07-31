package com.jstarcraft.core.storage.berkeley.migration;

import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;

/**
 * 忽略转换器
 * 
 * @author Birdy
 *
 */
public class IgnoreConverter implements MigrationConverter {

    @Override
    public boolean convert(MigrationContext context, RawStore oldRawStore, RawStore newRawStore, RawObject oldObject, RawObject newObject) throws Exception {
        return false;
    }

}
