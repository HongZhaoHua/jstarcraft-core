package com.jstarcraft.core.codec;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.specification.CodecDefinition;

public class CodecDefinitionTestCase {

	@Test
	public void test() throws Exception {
		Collection<Type> protocolClasses = new LinkedList<>();
		protocolClasses.add(MockComplexObject.class);
		protocolClasses.add(MockEnumeration.class);
		CodecDefinition left = CodecDefinition.instanceOf(protocolClasses);

		// 测试协议的序列化与反序列化
		byte[] bytes = CodecDefinition.toBytes(left);
		CodecDefinition right = CodecDefinition.fromBytes(bytes);

		Assert.assertThat(right, CoreMatchers.equalTo(left));
		File file = new File("codec/definition");
		FileUtils.deleteQuietly(file);
		FileUtils.writeByteArrayToFile(file, bytes);
	}

}
