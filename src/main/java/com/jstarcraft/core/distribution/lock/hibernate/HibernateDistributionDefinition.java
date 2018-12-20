package com.jstarcraft.core.distribution.lock.hibernate;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.distribution.lock.DistributionDefinition;

/**
 * Hibernate分布式定义
 * 
 * @author Birdy
 *
 */
@Entity
@NamedQueries({

		@NamedQuery(name = HibernateDistributionDefinition.LOCK_HQL, query = "update HibernateDistributionDefinition clazz set clazz.most=:most where clazz.name=:name and clazz.most<=:now"),

		@NamedQuery(name = HibernateDistributionDefinition.UNLOCK_HQL, query = "update HibernateDistributionDefinition clazz set clazz.most=:now where clazz.name=:name and clazz.most=:most and clazz.most>:now"), })
public class HibernateDistributionDefinition implements CacheObject<String> {

	public static final String LOCK_HQL = "HibernateDistributionDefinition.lock";

	public static final String UNLOCK_HQL = "HibernateDistributionDefinition.unlock";

	/** 锁名称 */
	@Id
	private String name;

	/** 最多锁定到指定的时间(必选) */
	private Instant most;

	HibernateDistributionDefinition() {
	}

	public HibernateDistributionDefinition(DistributionDefinition definition) {
		this(definition.getName(), definition.getMost());
	}

	public HibernateDistributionDefinition(String name, Instant most) {
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
