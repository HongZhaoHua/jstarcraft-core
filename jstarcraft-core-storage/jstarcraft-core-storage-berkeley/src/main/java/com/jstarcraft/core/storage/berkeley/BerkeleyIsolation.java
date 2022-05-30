package com.jstarcraft.core.storage.berkeley;

import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.TransactionConfig;

/**
 * Berkeley隔离级别
 * 
 * @author Birdy
 *
 */
public enum BerkeleyIsolation {

    READ_UNCOMMITTED(1), READ_COMMITTED(2), REPEATABLE_READ(3), SERIALIZABLE(4);

    /** 游标模型 */
    private final CursorConfig cursorModel;

    /** 锁模型 */
    private final LockMode lockMode;

    /** 事务模型 */
    private final TransactionConfig transactionModel;

    private BerkeleyIsolation(int code) {
        transactionModel = new TransactionConfig();
        switch (code) {
        case 1:
            cursorModel = CursorConfig.READ_UNCOMMITTED;
            transactionModel.setReadUncommitted(true);
            lockMode = LockMode.READ_UNCOMMITTED;
            break;
        case 2:
            cursorModel = CursorConfig.READ_COMMITTED;
            transactionModel.setReadCommitted(true);
            lockMode = LockMode.READ_COMMITTED;
            break;
        case 3:
            cursorModel = CursorConfig.DEFAULT;
            lockMode = LockMode.DEFAULT;
            break;
        case 4:
            cursorModel = CursorConfig.DEFAULT;
            lockMode = LockMode.RMW;
            transactionModel.setSerializableIsolation(true);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public CursorConfig getCursorModel() {
        return cursorModel;
    }

    public LockMode getLockMode() {
        return lockMode;
    }

    public TransactionConfig getTransactionModel() {
        return transactionModel;
    }

}
