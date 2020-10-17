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
 * etcd事务管理器
 * 
 * @author Birdy
 *
 */
public class EtcdTransactionManager extends TransactionManager {

    private class Holder {

        private final long lease;

        private final ByteSequence lock;

        private Holder(long lease, ByteSequence lock) {
            this.lease = lease;
            this.lock = lock;
        }

        public long getLease() {
            return lease;
        }

        public ByteSequence getLock() {
            return lock;
        }

    }

    private ThreadLocal<Holder> holders = new ThreadLocal<>();

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
            long lease = etcd.getLeaseClient().grant(TimeUnit.SECONDS.convert(expire, TimeUnit.MILLISECONDS)).get().getID();
            try {
                ByteSequence lock = etcd.getLockClient().lock(ByteSequence.from(key.getBytes(StringUtility.CHARSET)), lease).get(1000, TimeUnit.MILLISECONDS).getKey();
                Holder holder = new Holder(lease, lock);
                holders.set(holder);
            } catch (Exception exception) {
                etcd.getLeaseClient().revoke(lease).get();
                throw new TransactionLockException();
            }
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
            Holder holder = holders.get();
            long lease = holder.getLease();
            ByteSequence lock = holder.getLock();
            holders.remove();
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
