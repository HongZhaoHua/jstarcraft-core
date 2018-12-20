package com.jstarcraft.core.distribution.identity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.distribution.identity.IdentityDefinition;
import com.jstarcraft.core.distribution.identity.IdentitySection;
import com.jstarcraft.core.utility.StringUtility;

public class IdentityDefinitionTestCase {

	@Test
	public void test() {
		LinkedHashMap<String, Integer> configuration = new LinkedHashMap<>();
		configuration.put("partition", 10);
		try {
			// 测试不够63位的情况
			new IdentityDefinition(configuration);
			Assert.fail();
		} catch (IllegalArgumentException exception) {
		}

		configuration.put("instant", 41);
		configuration.put("sequence", 12);
		IdentityDefinition definition = new IdentityDefinition(configuration);
		List<IdentitySection> sections = definition.getSections();
		Assert.assertThat(sections.size(), CoreMatchers.equalTo(configuration.size()));

		int index = 0;
		long[] values = new long[configuration.size()];
		for (Entry<String, Integer> entry : configuration.entrySet()) {
			// 测试分段的掩码
			int value = entry.getValue();
			IdentitySection section = sections.get(index);
			Assert.assertThat(Long.toBinaryString(section.getMask()), CoreMatchers.equalTo(StringUtility.repeat('1', value)));
			values[index] = value;
			index++;
		}

		// 测试制造标识与分析标识
		long identity = definition.make(values);
		Assert.assertThat(definition.parse(identity), CoreMatchers.equalTo(values));

		try {
			// 测试包含0位的情况
			configuration.put("error", 0);
			new IdentityDefinition(configuration);
			Assert.fail();
		} catch (IllegalArgumentException exception) {
		}
		
		try {
			// 测试超过63位的情况
			configuration.put("error", 1);
			new IdentityDefinition(configuration);
			Assert.fail();
		} catch (IllegalArgumentException exception) {
		}
	}

}
