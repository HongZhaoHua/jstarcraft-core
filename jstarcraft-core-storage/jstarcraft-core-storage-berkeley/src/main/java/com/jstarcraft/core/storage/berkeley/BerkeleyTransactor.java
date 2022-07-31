package com.jstarcraft.core.storage.berkeley;

import java.util.HashMap;

import com.sleepycat.je.Transaction;

/**
 * Berkeley事务器
 * 
 * @author Birdy
 *
 */
public class BerkeleyTransactor {

    private final BerkeleyIsolation isolation;

    private final Transaction transaction;

    private final HashMap<BerkeleyIdentification, BerkeleyVersion> versions = new HashMap<>();

    public BerkeleyTransactor(BerkeleyIsolation isolation, Transaction transaction) {
        this.isolation = isolation;
        this.transaction = transaction;
    }

    public BerkeleyIsolation getIsolation() {
        return isolation;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public boolean isComplete() {
        return !transaction.isValid();
    }

    public void abort() {
        transaction.abort();
    }

    public void commit() {
        transaction.commit();
    }

    /**
     * 设置版本信息
     * 
     * @param versions
     */
    void setVersions(BerkeleyVersion... informations) {
        for (BerkeleyVersion version : informations) {
            versions.put(version.getIdentification(), version);
        }
    }

    /**
     * 获取版本信息
     * 
     * @return
     */
    BerkeleyVersion[] getVersions() {
        return versions.values().toArray(new BerkeleyVersion[versions.size()]);
    }

}
