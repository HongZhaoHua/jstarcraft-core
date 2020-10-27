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

    private ThreadLocal<Long> holders = new ThreadLocal<>();

    private Client etcd;

    public EtcdTransactionManager(Client etcd) {
        this.etcd = etcd;
    }

    @Override
    public void lock(TransactionDefinition definition) {
        // 尝试加锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        int expire = Long.valueOf(value - System.currentTimeMillis()).intValue();
        try {
            long lease = etcd.getLeaseClient().grant(TimeUnit.SECONDS.convert(expire, TimeUnit.MILLISECONDS)).get().getID();
            try {
                // 租约加锁成功则保存
                etcd.getLockClient().lock(ByteSequence.from(key.getBytes(StringUtility.CHARSET)), lease).get(1000, TimeUnit.MILLISECONDS).getKey();
                holders.set(lease);
            } catch (Exception exception) {
                // 租约加锁失败则释放
                etcd.getLeaseClient().revoke(lease).get();
                throw new TransactionLockException();
            }
        } catch (Exception exception) {
            throw new TransactionLockException(exception);
        }
    }

    @Override
    public void unlock(TransactionDefinition definition) {
        // 尝试解锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        try {
            long lease = holders.get();
            holders.remove();
            long ttl = etcd.getLeaseClient().timeToLive(lease, LeaseOption.DEFAULT).get().getTTl();
            // 根据TTL判断租约状态
            if (ttl > 0) {
                etcd.getLeaseClient().revoke(lease).get();
            } else {
                throw new TransactionUnlockException();
            }
        } catch (Exception exception) {
            throw new TransactionUnlockException(exception);
        }
    }

}
