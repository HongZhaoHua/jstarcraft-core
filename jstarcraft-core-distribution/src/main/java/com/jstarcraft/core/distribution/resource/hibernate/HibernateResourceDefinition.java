package com.jstarcraft.core.distribution.resource.hibernate;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.distribution.resource.ResourceDefinition;

/**
 * Hibernate分布式定义
 * 
 * @author Birdy
 *
 */
@Entity
@NamedQueries({

        @NamedQuery(name = HibernateResourceDefinition.LOCK_HQL, query = "UPDATE HibernateDistributionDefinition clazz SET clazz.most=:most WHERE clazz.name=:name AND clazz.most<=:now"),

        @NamedQuery(name = HibernateResourceDefinition.UNLOCK_HQL, query = "UPDATE HibernateDistributionDefinition clazz SET clazz.most=:now WHERE clazz.name=:name AND clazz.most=:most AND clazz.most>:now"), })
public class HibernateResourceDefinition implements IdentityObject<String> {

    public static final String LOCK_HQL = "HibernateDistributionDefinition.lock";

    public static final String UNLOCK_HQL = "HibernateDistributionDefinition.unlock";

    /** 锁名称 */
    @Id
    private String name;

    /** 最多锁定到指定的时间(必选) */
    private Instant most;

    HibernateResourceDefinition() {
    }

    public HibernateResourceDefinition(ResourceDefinition definition) {
        this(definition.getName(), definition.getMost());
    }

    public HibernateResourceDefinition(String name, Instant most) {
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
