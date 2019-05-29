package com.jstarcraft.core.transaction.balance;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.transaction.balance.HashCycle;
import com.jstarcraft.core.transaction.exception.TransactionException;
import com.jstarcraft.core.utility.HashUtility;

public class HashCycleTestCase {

	@Test
	public void test() {
		// 此处故意使用叠加哈希函数便于实现控制
		HashCycle<String> cycle = new HashCycle<>(HashUtility::additiveStringHash32);
		// 20以前一定不存在哈希冲突
		for (int index = 0; index < 20; index++) {
			cycle.createNode(String.valueOf(index), String.valueOf(index));
		}
		Assert.assertThat(cycle.getSize(), CoreMatchers.equalTo(20));
		
		// 节点已存在
		try {
			cycle.createNode("0", "0");
			Assert.fail();
		} catch (TransactionException exception) {
		}

		// 20与11存在哈希冲突
		try {
			cycle.createNode("20", "20");
			Assert.fail();
		} catch (TransactionException exception) {
		}

		// 选择节点(按照顺时针)
		Assert.assertThat(cycle.selectNode("20"), CoreMatchers.equalTo("12"));
		Assert.assertThat(cycle.selectNode("28"), CoreMatchers.equalTo("0"));

		// 节点不存在
		try {
			cycle.deleteNode("20");
			Assert.fail();
		} catch (TransactionException exception) {
		}

		for (int index = 0; index < 20; index++) {
			cycle.deleteNode(String.valueOf(index));
		}

		Assert.assertThat(cycle.getSize(), CoreMatchers.equalTo(0));
	}

}
