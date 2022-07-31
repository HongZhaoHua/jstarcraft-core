package com.jstarcraft.core.storage.berkeley.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.BerkeleyTransactor;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyTransaction;
import com.sleepycat.je.LockConflictException;

/**
 * 事务上下文对象
 * 
 * @author Birdy
 */
public class TransactionContext {

    private static final Logger logger = LoggerFactory.getLogger(TransactionContext.class);

    /** 访问器 */
    private final BerkeleyAccessor accessor;
    /** 注解 */
    private final BerkeleyTransaction annotation;
    /** 计数器 */
    private int count = 0;
    /** 异常 */
    private Throwable exception;
    /** 事务 */
    private BerkeleyTransactor transactor;
    /** 尝试次数 */
    private int triedTimes = 0;

    public TransactionContext(BerkeleyAccessor accessor, BerkeleyTransaction annotation) {
        this.accessor = accessor;
        this.annotation = annotation;
    }

    public int getCount() {
        return count;
    }

    public Throwable getException() {
        return exception;
    }

    void increase() {
        if (count == 0) {
            // 事务开启
            transactor = accessor.openTransactor(annotation.isolation());
        }
        count++; // 计数器加1
    }

    void decrease(Throwable throwable) {
        exception = throwable;
        count--; // 计数器减1
        if (count == 0) {
            boolean interrupt = exception != null;
            // 事务关闭
            accessor.closeTransactor(interrupt);
        }
    }

    /**
     * 检查是否需要尝试
     * 
     * @return true需要尝试,false不需要尝试.
     */
    boolean retry() {
        // 是否为嵌套事务
        if (count != 0) {
            return false;
        }

        if (triedTimes < annotation.tryTimes()) {
            // 如果为锁冲突异常则等待指定时间.
            if (exception instanceof LockConflictException) {
                long wait = System.currentTimeMillis() + annotation.conflictWait();
                while (wait > System.currentTimeMillis()) {
                    Thread.yield();
                }
            }
            exception = null;
            triedTimes++;
            transactor = null;
            return true;
        }
        return false;
    }

}
