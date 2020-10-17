package com.jstarcraft.core.transaction.etcd;

import java.util.concurrent.TimeUnit;

import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;
import com.jstarcraft.core.utility.StringUtility;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.LeaseOption;

/**
 * Etcd事务管理器
 * 
 * @author Birdy
 *
 */
// TODO 此类需要重构
public class EtcdTransactionManager extends TransactionManager {

    private long lease;

    private ByteSequence lock;

    private Client etcd;

    public EtcdTransactionManager(Client etcd) {
        this.etcd = etcd;
    }

    @Override
    protected void lock(TransactionDefinition definition) {
        // 尝试加锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        int expire = Long.valueOf(value - System.currentTimeMillis()).intValue();
        try {
            lease = etcd.getLeaseClient().grant(TimeUnit.SECONDS.convert(expire, TimeUnit.MILLISECONDS)).get().getID();
            lock = etcd.getLockClient().lock(ByteSequence.from(key.getBytes(StringUtility.CHARSET)), lease).get(1000, TimeUnit.MILLISECONDS).getKey();
        } catch (Exception exception) {
            throw new TransactionLockException(exception);
        }
    }

    @Override
    protected void unlock(TransactionDefinition definition) {
        // 尝试解锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        try {
            long ttl = etcd.getLeaseClient().timeToLive(lease, LeaseOption.DEFAULT).get().getTTl();
            if (ttl > 0) {
                etcd.getLeaseClient().revoke(lease).get();
                etcd.getLockClient().unlock(lock).get();
            } else {
                throw new TransactionUnlockException();
            }
        } catch (Exception exception) {
            throw new TransactionUnlockException(exception);
        }
    }

}
