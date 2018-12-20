package com.jstarcraft.core.cache.proxy;

import com.jstarcraft.core.cache.CacheObject;

public class MockProxyManager implements ProxyManager<Integer, CacheObject<Integer>> {

	private int modifyDatas;

	public int getModifyDatas() {
		return modifyDatas;
	}

	@Override
	public void modifyInstance(CacheObject<Integer> object) {
		this.modifyDatas++;
	}

}
