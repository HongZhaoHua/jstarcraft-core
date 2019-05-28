package com.jstarcraft.core.distribution.resource.hibernate;

import java.time.Instant;
import java.util.HashMap;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.resource.ResourceDefinition;
import com.jstarcraft.core.distribution.resource.ResourceManager;
import com.jstarcraft.core.orm.hibernate.HibernateAccessor;

/**
 * Hibernate分布式管理器
 * 
 * @author Birdy
 *
 */
public class HibernateResourceManager extends ResourceManager {

	private HibernateAccessor accessor;

	public HibernateResourceManager(HibernateAccessor accessor) {
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
				HibernateResourceDefinition definition = new HibernateResourceDefinition(name, now);
				accessor.create(HibernateResourceDefinition.class, definition);
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
				accessor.delete(HibernateResourceDefinition.class, name);
				count++;
			} catch (Exception exception) {
			}
		}
		return count;
	}

	@Override
	protected void lock(ResourceDefinition definition) {
		String name = definition.getName();
		Instant most = definition.getMost();
		Instant now = Instant.now();
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("name", name);
		parameters.put("most", most);
		parameters.put("now", now);
		Integer count = Integer.class.cast(accessor.query(HibernateResourceDefinition.LOCK_HQL, null, null, parameters).get(0));
		if (count != 1) {
			throw new DistributionLockException();
		}
	}

	@Override
	protected void unlock(ResourceDefinition definition) {
		String name = definition.getName();
		Instant most = definition.getMost();
		Instant now = Instant.now();
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("name", name);
		parameters.put("most", most);
		parameters.put("now", now);
		Integer count = Integer.class.cast(accessor.query(HibernateResourceDefinition.UNLOCK_HQL, null, null, parameters).get(0));
		if (count != 1) {
			throw new DistributionUnlockException();
		}
	}

}
