package com.jstarcraft.core.cache.proxy;

import com.jstarcraft.core.utility.IdentityObject;

public class MockProxyManager implements ProxyManager<Integer, IdentityObject<Integer>> {

	private int modifyDatas;

	public int getModifyDatas() {
		return modifyDatas;
	}

	@Override
	public void modifyInstance(IdentityObject<Integer> object) {
		this.modifyDatas++;
	}

}
