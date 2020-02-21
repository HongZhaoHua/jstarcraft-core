package com.jstarcraft.core.storage.berkeley.migration;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 迁移计数器
 * 
 * @author Birdy
 *
 */
public class MigrationCounter {

    /** 读入记录数量 */
    private final AtomicLong readRecordNumber = new AtomicLong();

    /** 写出记录数量 */
    private final AtomicLong writeRecordNumber = new AtomicLong();

    /** 当前任务数量 */
    private final AtomicLong currentTaskNumber = new AtomicLong();

    public AtomicLong getReadRecordNumber() {
        return readRecordNumber;
    }

    public AtomicLong getWriteRecordNumber() {
        return writeRecordNumber;
    }

    public AtomicLong getCurrentTaskNumber() {
        return currentTaskNumber;
    }

}
