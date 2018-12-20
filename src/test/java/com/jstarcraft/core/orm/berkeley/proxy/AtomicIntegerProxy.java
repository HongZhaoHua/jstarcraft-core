package com.jstarcraft.core.orm.berkeley.proxy;

import java.util.concurrent.atomic.AtomicInteger;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

@Persistent(proxyFor = AtomicInteger.class)
public class AtomicIntegerProxy implements PersistentProxy<AtomicInteger> {

	private int value;

	public final AtomicInteger convertProxy() {
		return new AtomicInteger(value);
	}

	public final void initializeProxy(AtomicInteger instance) {
		value = instance.get();
	}

}