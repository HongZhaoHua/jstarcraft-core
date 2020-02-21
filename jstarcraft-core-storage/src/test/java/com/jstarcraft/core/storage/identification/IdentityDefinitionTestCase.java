package com.jstarcraft.core.storage.identification;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.storage.identification.IdentityDefinition;
import com.jstarcraft.core.storage.identification.IdentitySection;
import com.jstarcraft.core.utility.StringUtility;

public class IdentityDefinitionTestCase {

    @Test
    public void test() {
        try {
            // 测试不够63位的情况
            new IdentityDefinition(10);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }

        int[] configuration = new int[] { 10, 41, 12 };
        IdentityDefinition definition = new IdentityDefinition(configuration);
        List<IdentitySection> sections = definition.getSections();
        Assert.assertThat(sections.size(), CoreMatchers.equalTo(3));

        int index = 0;
        long[] values = new long[3];
        for (int value : configuration) {
            // 测试分段的掩码
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
            new IdentityDefinition(0, 10, 41, 12);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }

        try {
            // 测试超过63位的情况
            new IdentityDefinition(1, 10, 41, 12);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
    }

}
