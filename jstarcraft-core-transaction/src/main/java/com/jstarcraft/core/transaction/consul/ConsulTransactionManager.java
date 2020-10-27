package com.jstarcraft.core.transaction.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;
import com.ecwid.consul.v1.session.model.Session;
import com.ecwid.consul.v1.session.model.Session.Behavior;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;

/**
 * Consul事务管理器
 * 
 * <pre>
 * 注意:
 * <a href="https://www.consul.io/docs/internals/sessions">由于Consul的会话机制</a>
 * <a href="https://github.com/hashicorp/consul/issues/1172">会话会保持TTL的2倍</a>
 * </pre>
 * 
 * @author Birdy
 *
 */
public class ConsulTransactionManager extends TransactionManager {

    private ThreadLocal<String> holders = new ThreadLocal<>();

    private ConsulClient consul;

    private long ttl;

    public ConsulTransactionManager(ConsulClient consul) {
        this(consul, 10);
    }

    public ConsulTransactionManager(ConsulClient consul, long ttl) {
        this.consul = consul;
        this.ttl = ttl;
    }

    @Override
    public void lock(TransactionDefinition definition) {
        // 尝试加锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        long expire = Math.max((value - System.currentTimeMillis()) / 1000L, ttl);
        NewSession session = new NewSession();
        session.setName(key);
        session.setLockDelay(0);
        session.setBehavior(Behavior.DELETE);
        session.setTtl(expire + "s");
        String lease = consul.sessionCreate(session, QueryParams.DEFAULT).getValue();
        PutParams parameters = new PutParams();
        parameters.setAcquireSession(lease);
        if (consul.setKVValue(key, key, parameters).getValue()) {
            holders.set(lease);
        } else {
            consul.sessionDestroy(lease, QueryParams.DEFAULT);
            throw new TransactionLockException();
        }
    }

    @Override
    public void unlock(TransactionDefinition definition) {
        // 尝试解锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        try {
            String lease = holders.get();
            holders.remove();
            Session session = consul.getSessionInfo(lease, QueryParams.DEFAULT).getValue();
            if (session != null) {
                consul.sessionDestroy(lease, QueryParams.DEFAULT);
            } else {
                throw new TransactionUnlockException();
            }
        } catch (Exception exception) {
            throw new TransactionUnlockException(exception);
        }
    }

}
