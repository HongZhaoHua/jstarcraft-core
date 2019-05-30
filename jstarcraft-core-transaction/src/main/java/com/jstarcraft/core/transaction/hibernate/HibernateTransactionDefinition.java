package com.jstarcraft.core.transaction.hibernate;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.transaction.TransactionDefinition;

/**
 * Hibernate事务定义
 * 
 * @author Birdy
 *
 */
@Entity
@NamedQueries({

        @NamedQuery(name = HibernateTransactionDefinition.LOCK_HQL, query = "UPDATE HibernateTransactionDefinition clazz SET clazz.most=:most WHERE clazz.name=:name AND clazz.most<=:now"),

        @NamedQuery(name = HibernateTransactionDefinition.UNLOCK_HQL, query = "UPDATE HibernateTransactionDefinition clazz SET clazz.most=:now WHERE clazz.name=:name AND clazz.most=:most AND clazz.most>:now"), })
public class HibernateTransactionDefinition implements IdentityObject<String> {

    public static final String LOCK_HQL = "HibernateTransactionDefinition.lock";

    public static final String UNLOCK_HQL = "HibernateTransactionDefinition.unlock";

    /** 锁名称 */
    @Id
    private String name;

    /** 最多锁定到指定的时间(必选) */
    private Instant most;

    HibernateTransactionDefinition() {
    }

    public HibernateTransactionDefinition(TransactionDefinition definition) {
        this(definition.getName(), definition.getMost());
    }

    public HibernateTransactionDefinition(String name, Instant most) {
        this.name = name;
        this.most = most;
    }

    @Override
    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Instant getMost() {
        return most;
    }

}
