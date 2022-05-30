package com.jstarcraft.core.storage.berkeley;

/**
 * 事务监控器
 * 
 * @author Birdy
 */
public interface BerkeleyMonitor {

    /** 开启通知 */
    void notifyOpen(BerkeleyTransactor transactor);

    /** 成功通知 */
    void notifySuccess(BerkeleyTransactor transactor);

    /** 失败通知 */
    void notifyFailure(BerkeleyTransactor transactor);

    /** 关闭通知 */
    void notifyClose(BerkeleyTransactor transactor);

}
