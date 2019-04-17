package com.jstarcraft.core.distribution.lock.hibernate;

import java.time.Instant;
import java.util.HashMap;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.orm.hibernate.HibernateAccessor;

/**
 * Hibernate分布式管理器
 * 
 * @author Birdy
 *
 */
public class HibernateDistributionManager extends DistributionManager {

	private HibernateAccessor accessor;

	public HibernateDistributionManager(HibernateAccessor accessor) {
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
				HibernateDistributionDefinition definition = new HibernateDistributionDefinition(name, now);
				accessor.create(HibernateDistributionDefinition.class, definition);
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
				accessor.delete(HibernateDistributionDefinition.class, name);
				count++;
			} catch (Exception exception) {
			}
		}
		return count;
	}

	@Override
	protected void lock(DistributionDefinition definition) {
		String name = definition.getName();
		Instant most = definition.getMost();
		Instant now = Instant.now();
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("name", name);
		parameters.put("most", most);
		parameters.put("now", now);
		Integer count = Integer.class.cast(accessor.query(HibernateDistributionDefinition.LOCK_HQL, null, null, parameters).get(0));
		if (count != 1) {
			throw new DistributionLockException();
		}
	}

	@Override
	protected void unlock(DistributionDefinition definition) {
		String name = definition.getName();
		Instant most = definition.getMost();
		Instant now = Instant.now();
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("name", name);
		parameters.put("most", most);
		parameters.put("now", now);
		Integer count = Integer.class.cast(accessor.query(HibernateDistributionDefinition.UNLOCK_HQL, null, null, parameters).get(0));
		if (count != 1) {
			throw new DistributionUnlockException();
		}
	}

}
