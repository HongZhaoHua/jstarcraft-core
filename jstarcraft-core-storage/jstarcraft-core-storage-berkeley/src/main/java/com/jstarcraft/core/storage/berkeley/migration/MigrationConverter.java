package com.jstarcraft.core.storage.berkeley.migration;

import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;

/**
 * 迁移转换器
 * 
 * @author Birdy
 *
 */
public interface MigrationConverter {

    /**
     * 转换(将旧数据转换为新数据)
     * 
     * @param context   转换过程的上下文对象
     * @param newObject 新数据
     * @param oldObject 旧数据
     * @return 迁移则返回true,忽略则返回false;
     * @throws Exception
     */
    public boolean convert(MigrationContext context, RawStore oldRawStore, RawStore newRawStore, RawObject oldObject, RawObject newObject) throws Exception;

}
