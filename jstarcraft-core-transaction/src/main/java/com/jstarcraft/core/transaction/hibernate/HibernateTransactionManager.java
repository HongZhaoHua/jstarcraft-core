package com.jstarcraft.core.transaction.hibernate;

import java.time.Instant;
import java.util.HashMap;

import com.jstarcraft.core.storage.hibernate.HibernateAccessor;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;

/**
 * Hibernate事务管理器
 * 
 * @author Birdy
 *
 */
public class HibernateTransactionManager extends TransactionManager {

    private HibernateAccessor accessor;

    public HibernateTransactionManager(HibernateAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * 尝试创建指定的锁
     * 
     * @param names
     * @return
     */
    public int create(String... names) {
        int count = 0;
        Instant now = Instant.now();
        for (String name : names) {
            try {
                HibernateTransactionDefinition definition = new HibernateTransactionDefinition(name, now);
                accessor.createInstance(HibernateTransactionDefinition.class, definition);
                count++;
            } catch (Exception exception) {
            }
        }
        return count;
    }

    /**
     * 尝试删除指定的锁
     * 
     * @param names
     * @return
     */
    public int delete(String... names) {
        int count = 0;
        for (String name : names) {
            try {
                accessor.deleteInstance(HibernateTransactionDefinition.class, name);
                count++;
            } catch (Exception exception) {
            }
        }
        return count;
    }

    @Override
    protected void lock(TransactionDefinition definition) {
        String name = definition.getName();
        Instant most = definition.getMost();
        Instant now = Instant.now();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("most", most);
        parameters.put("now", now);
        Integer count = Integer.class.cast(accessor.queryDatas(HibernateTransactionDefinition.LOCK_HQL, null, null, parameters).get(0));
        if (count != 1) {
            throw new TransactionLockException();
        }
    }

    @Override
    protected void unlock(TransactionDefinition definition) {
        String name = definition.getName();
        Instant most = definition.getMost();
        Instant now = Instant.now();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("most", most);
        parameters.put("now", now);
        Integer count = Integer.class.cast(accessor.queryDatas(HibernateTransactionDefinition.UNLOCK_HQL, null, null, parameters).get(0));
        if (count != 1) {
            throw new TransactionUnlockException();
        }
    }

}
